import React, { useState, useEffect, useRef } from 'react';
import html2canvas from 'html2canvas';
import { jsPDF } from 'jspdf';
import IosPickerField from '../components/IosPickerField';
import { useFsfDialog } from '../components/FsfDialogProvider';
import './TimetableManager.css';

const DEPT_OPTIONS = [
  { value: 'CS',  label: 'BS Computer Science' },
  { value: 'SE',  label: 'BS Software Engineering' },
  { value: 'DS',  label: 'BS Data Science' },
  { value: 'AI',  label: 'BS Artificial Intelligence' },
  { value: 'CYS', label: 'BS Cybersecurity' },
];

const BATCH_OPTIONS = [
  { value: '25', label: '2025 (Semester 2)' },
  { value: '24', label: '2024 (Semester 4)' },
  { value: '23', label: '2023 (Semester 6)' },
  { value: '22', label: '2022 (Semester 8)' },
];

const SECTION_OPTIONS = 'ABCDEFGHIJ'.split('').map(s => ({ value: s, label: `Section ${s}` }));

const DAYS = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY'];

// All 8 standard NUCES lecture/lab periods (each 1h30m)
const ALL_SLOTS = [
  '08:30-10:00',
  '10:00-11:30',
  '11:30-13:00',
  '13:00-14:30',
  '14:30-16:00',
  '16:00-17:30',
  '17:30-19:00',
  '19:00-20:30',
];

// Pastel color palette for courses (consistent per course name)
const COURSE_COLORS = [
  '#4f46e5', '#0891b2', '#059669', '#d97706', '#dc2626',
  '#7c3aed', '#db2777', '#0284c7', '#16a34a', '#ea580c',
  '#6366f1', '#06b6d4', '#10b981', '#f59e0b', '#ef4444',
];
const courseColorCache = {};
let colorIdx = 0;
function getCourseColor(courseName) {
  const key = courseName.toLowerCase().replace(/\s+lab\s*$/i, '').trim();
  if (!courseColorCache[key]) {
    courseColorCache[key] = COURSE_COLORS[colorIdx % COURSE_COLORS.length];
    colorIdx++;
  }
  return courseColorCache[key];
}

const TimetableManager = ({ user }) => {
  const { showAlert } = useFsfDialog();
  const exportRef = useRef(null);

  const [dept, setDept]       = useState('CS');
  const [batch, setBatch]     = useState('24');
  const [section, setSection] = useState('A');

  const [timetable, setTimetable] = useState([]);
  const [loading, setLoading]     = useState(false);
  const [error, setError]         = useState(null);

  const [selectedFile, setSelectedFile] = useState(null);
  const [uploadUrl, setUploadUrl]       = useState('');
  const [uploading, setUploading]       = useState(false);
  const [uploadError, setUploadError]   = useState(null);

  useEffect(() => { loadTimetable(); }, [dept, batch, section]);

  const loadTimetable = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await fetch(
        `http://localhost:8080/api/timetable/section?department=${dept}&batch=${batch}&section=${section}`
      );
      if (!res.ok) throw new Error('Failed to fetch timetable');
      const data = await res.json();
      setTimetable(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!uploadUrl && !selectedFile) {
      setUploadError('Please provide a URL or choose a file.');
      return;
    }
    setUploading(true);
    setUploadError(null);
    try {
      let res;
      if (selectedFile) {
        const form = new FormData();
        form.append('file', selectedFile);
        form.append('ownerName', user.name);
        form.append('ownerEmail', user.email);
        res = await fetch('http://localhost:8080/api/timetable/upload-file', { method: 'POST', body: form });
      } else {
        res = await fetch(
          `http://localhost:8080/api/timetable/upload?url=${encodeURIComponent(uploadUrl)}&ownerName=${encodeURIComponent(user.name)}&ownerEmail=${encodeURIComponent(user.email)}`,
          { method: 'POST' }
        );
      }
      if (!res.ok) throw new Error(await res.text() || 'Upload failed');
      const result = await res.json();
      await showAlert({ title: 'Upload complete', message: `Timetable loaded — ${result.count || ''} entries saved.` });
      setUploadUrl(''); setSelectedFile(null);
      loadTimetable();
    } catch (err) {
      setUploadError(err.message);
    } finally {
      setUploading(false);
    }
  };

  // ── Build weekly grid ──────────────────────────────────────────────────────
  // Always use all 8 standard NUCES periods as rows so lab rowspan=2
  // always has a physical next row to expand into.
  // Only trim trailing empty rows that have no data at all.
  const getEntry = (day, slot) =>
    timetable.find(t =>
      t.dayOfWeek === day &&
      (t.startTime + '-' + t.endTime) === slot
    ) || null;

  // Build covered-by-lab set to know which slots are "second half" of a lab
  const labSecondSlots = new Set();
  ALL_SLOTS.forEach((slot, i) => {
    if (i + 1 >= ALL_SLOTS.length) return;
    DAYS.forEach(day => {
      const e = getEntry(day, slot);
      if (e && /lab/i.test(e.courseName)) {
        labSecondSlots.add(`${day}|${ALL_SLOTS[i + 1]}`);
      }
    });
  });

  // Show only slots that have an entry OR are a lab-second-slot (needed for span)
  // Drop completely empty trailing slots to keep the grid compact.
  const timeSlots = ALL_SLOTS.filter(slot => {
    const hasEntry = DAYS.some(day => getEntry(day, slot) !== null);
    const isLabSecond = DAYS.some(day => labSecondSlots.has(`${day}|${slot}`));
    return hasEntry || isLabSecond;
  });

  // ── Export helpers ─────────────────────────────────────────────────────────
  const doExport = async (mode) => {
    const el = exportRef.current;
    if (!el) return;
    el.style.display = 'block';
    await new Promise(r => setTimeout(r, 120));
    const canvas = await html2canvas(el, { scale: 2, useCORS: true, backgroundColor: '#0f0f13' });
    el.style.display = 'none';
    if (mode === 'img') {
      const link = document.createElement('a');
      link.href = canvas.toDataURL('image/png');
      link.download = `Timetable_${dept}${batch}${section}.png`;
      link.click();
    } else {
      // Create PDF sized exactly to the canvas — no white bottom padding
      const imgData = canvas.toDataURL('image/png');
      const pxToMm = 0.264583; // 1px = 0.264583mm
      const pdfW = (canvas.width  / 2) * pxToMm;  // divide by scale
      const pdfH = (canvas.height / 2) * pxToMm;
      const pdf = new jsPDF({
        orientation: pdfW > pdfH ? 'landscape' : 'portrait',
        unit: 'mm',
        format: [pdfW, pdfH],
      });
      pdf.addImage(imgData, 'PNG', 0, 0, pdfW, pdfH);
      pdf.save(`Timetable_${dept}${batch}${section}.pdf`);
    }
  };

  const deptLabel = DEPT_OPTIONS.find(d => d.value === dept)?.label || dept;
  const batchLabel = BATCH_OPTIONS.find(b => b.value === batch)?.label || batch;

  return (
    <div className="tt-page">
      {/* ── Header ─────────────────────────────────────────────────────── */}
      <header className="tt-header">
        <div className="tt-header-text">
          <h2>Timetable</h2>
          <p>Weekly class schedule for any section</p>
        </div>
      </header>

      {/* ── Admin Upload ────────────────────────────────────────────────── */}
      {user?.role === 'ADMIN' && (
        <section className="tt-upload-card glass-card">
          <h3>Upload Timetable (Admin)</h3>
          {uploadError && <p className="tt-error">{uploadError}</p>}
          <form onSubmit={handleUpload} className="tt-upload-form">
            <div className="tt-upload-row">
              <label className="tt-upload-label">CSV URL (Google Sheet)</label>
              <input
                type="url" placeholder="https://docs.google.com/spreadsheets/…"
                value={uploadUrl}
                onChange={e => { setUploadUrl(e.target.value); if (e.target.value) setSelectedFile(null); }}
                className="tt-url-input"
              />
            </div>
            <div className="tt-upload-or">— or —</div>
            <div className="tt-upload-row">
              <label className="tt-upload-label">Upload Excel (.xlsx) / CSV</label>
              <label className="ios-file-field tt-file-pick">
                <input
                  type="file" className="ios-file-field-input"
                  accept=".csv,.xlsx,.xls"
                  onChange={e => { setSelectedFile(e.target.files?.[0] ?? null); if (e.target.files?.[0]) setUploadUrl(''); }}
                />
                <span className="ios-file-field-btn">Choose File</span>
                <span className="ios-file-field-name">{selectedFile?.name || 'No file chosen'}</span>
              </label>
            </div>
            <button type="submit" className="primary-btn" disabled={uploading}>
              {uploading ? 'Uploading…' : 'Upload & Replace Timetable'}
            </button>
          </form>
        </section>
      )}

      {/* ── Filters ─────────────────────────────────────────────────────── */}
      <section className="tt-filters glass-card">
        <div className="tt-filter-group">
          <label>Department</label>
          <IosPickerField value={dept} onChange={setDept} options={DEPT_OPTIONS} sheetTitle="Department" />
        </div>
        <div className="tt-filter-group">
          <label>Batch</label>
          <IosPickerField value={batch} onChange={setBatch} options={BATCH_OPTIONS} sheetTitle="Batch" />
        </div>
        <div className="tt-filter-group">
          <label>Section</label>
          <IosPickerField value={section} onChange={setSection} options={SECTION_OPTIONS} sheetTitle="Section" />
        </div>
        <button className="tt-refresh-btn" onClick={loadTimetable} disabled={loading}>
          {loading ? '⟳ Loading…' : '⟳ Refresh'}
        </button>
      </section>

      {error && <p className="tt-error">{error}</p>}

      {/* ── Weekly Grid ─────────────────────────────────────────────────── */}
      {loading ? (
        <div className="tt-loading">
          <div className="tt-spinner" />
          <p>Loading timetable…</p>
        </div>
      ) : timetable.length === 0 ? (
        <div className="tt-empty glass-card">
          <div className="tt-empty-icon">📅</div>
          <h3>No timetable found</h3>
          <p>No classes found for <strong>{deptLabel}</strong>, Batch <strong>20{batch}</strong>, Section <strong>{section}</strong>.</p>
          {user?.role === 'ADMIN' && <p>Upload the timetable Excel file above.</p>}
        </div>
      ) : (
        <>
          {/* Export buttons */}
          <div className="tt-export-bar">
            <span className="tt-export-label">
              {deptLabel} · 20{batch} · Section {section}
              <span className="tt-count-badge">{timetable.length} classes/week</span>
            </span>
            <div className="tt-export-btns">
              <button className="secondary-btn" onClick={() => doExport('img')}>⬇ Export PNG</button>
              <button className="secondary-btn" onClick={() => doExport('pdf')}>⬇ Export PDF</button>
            </div>
          </div>

          {/* Timetable grid */}
          <div className="tt-grid-wrapper">
            <div className="tt-grid-scroll">
              <table className="tt-grid">
                <thead>
                  <tr>
                    <th className="tt-th-time">Time</th>
                    {DAYS.map(d => (
                      <th key={d} className="tt-th-day">{d.charAt(0) + d.slice(1).toLowerCase()}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {timeSlots.length === 0 ? (
                    <tr><td colSpan={6} className="tt-no-slots">No time slots found</td></tr>
                  ) : (
                    timeSlots.map((slot, slotIdx) => (
                      <tr key={slot}>
                        <td className="tt-td-time">
                          <div className="tt-time-label">
                            {slot.split('-')[0]}
                            <span className="tt-time-sep">–</span>
                            {slot.split('-').slice(1).join('-')}
                          </div>
                        </td>
                        {DAYS.map(day => {
                          const isLabSecond = labSecondSlots.has(`${day}|${slot}`);
                          const entry = isLabSecond
                            // For the 2nd slot of a lab, find the original lab entry from the slot above
                            ? getEntry(day, timeSlots[slotIdx - 1])
                            : getEntry(day, slot);
                          const isLab = entry && /lab/i.test(entry.courseName);
                          const color = entry ? getCourseColor(entry.courseName) : null;
                          return (
                            <td
                              key={day}
                              className={`tt-td-cell${entry ? ' tt-td-filled' : ''}${isLab ? (isLabSecond ? ' tt-td-lab-cont' : ' tt-td-lab') : ''}`}
                            >
                              {entry && isLab ? (
                                <div className="tt-cell-card" style={{ borderLeftColor: color }}>
                                  <div className="tt-cell-course" style={{ color }}>
                                    {entry.courseName}
                                    <span className="tt-lab-badge">{isLabSecond ? 'LAB ▼' : 'LAB'}</span>
                                  </div>
                                  <div className="tt-cell-room">📍 {entry.roomNumber}</div>
                                  <div className="tt-cell-inst">👤 {entry.instructorName}</div>
                                  {!isLabSecond && <div className="tt-cell-duration">⏱ 3 hrs</div>}
                                </div>
                              ) : !isLab && entry ? (
                                <div className="tt-cell-card" style={{ borderLeftColor: color }}>
                                  <div className="tt-cell-course" style={{ color }}>{entry.courseName}</div>
                                  <div className="tt-cell-room">📍 {entry.roomNumber}</div>
                                  <div className="tt-cell-inst">👤 {entry.instructorName}</div>
                                </div>
                              ) : (
                                <div className="tt-cell-empty" />
                              )}
                            </td>
                          );
                        })}
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </>
      )}

      {/* ── HIDDEN EXPORT GRID (off-screen, rendered for html2canvas) ──── */}
      <div ref={exportRef} className="tt-export-hidden" style={{ display: 'none' }}>
        <div className="tt-export-header">
          <h1>FAST NUCES — Class Schedule</h1>
          <p>{deptLabel} &nbsp;·&nbsp; Batch 20{batch} &nbsp;·&nbsp; Section {section}</p>
        </div>
        <table className="tt-export-table">
          <thead>
            <tr>
              <th>Time</th>
              {DAYS.map(d => <th key={d}>{d}</th>)}
            </tr>
          </thead>
          <tbody>
            {timeSlots.map((slot, slotIdx) => (
              <tr key={slot}>
                <td className="tt-export-time">{slot}</td>
                {DAYS.map(day => {
                  const isLabSecond = labSecondSlots.has(`${day}|${slot}`);
                  const entry = isLabSecond
                    ? getEntry(day, timeSlots[slotIdx - 1])
                    : getEntry(day, slot);
                  const isLab = entry && /lab/i.test(entry.courseName);
                  const color = entry ? getCourseColor(entry.courseName) : null;
                  return (
                    <td key={day}
                        className={`tt-export-cell${entry ? ' tt-export-filled' : ''}`}
                        style={entry ? { borderLeftColor: color, opacity: isLabSecond ? 0.75 : 1 } : {}}>
                      {entry ? (
                        <>
                          <div style={{ fontWeight: 700, color, marginBottom: 3 }}>
                            {entry.courseName}{isLab ? (isLabSecond ? ' [LAB ▼]' : ' [LAB]') : ''}
                          </div>
                          <div style={{ fontSize: 11, opacity: 0.85 }}>{entry.roomNumber}</div>
                          <div style={{ fontSize: 10, fontStyle: 'italic', marginTop: 2 }}>{entry.instructorName}</div>
                        </>
                      ) : ''}
                    </td>
                  );
                })}
              </tr>
            ))}
          </tbody>
        </table>
        <div className="tt-export-footer">Generated by FAST Student Facilitator (FSF)</div>
      </div>
    </div>
  );
};

export default TimetableManager;
