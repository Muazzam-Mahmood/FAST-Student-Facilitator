import React from 'react';
import { fsfFetch } from '../utils/apiClient';
import {
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  BarChart,
  Bar,
  Cell,
  Legend
} from 'recharts';
import './AdminReportChart.css';

/**
 * AdminReportChart
 * 
 * A dynamic, visual representation of system growth and activity.
 * Replaces the static cards for a more premium "Command Center" feel.
 */
const AdminReportChart = ({ stats }) => {
  const [usageData, setUsageData] = React.useState([
    { name: 'Papers', hits: 0 },
    { name: 'Rides', hits: 0 },
    { name: 'Books', hits: 0 },
    { name: 'Map', hits: 0 }
  ]);
  const [loading, setLoading] = React.useState(true);

  React.useEffect(() => {
    const fetchStats = async () => {
      try {
        const res = await fsfFetch('http://localhost:8080/api/admin/analytics/feature-usage');
        const data = await res.json();
        
        // Map backend names to UI labels if needed
        const mapped = data.map(item => ({
          name: item.name,
          hits: parseInt(item.hits) || 0
        })).sort((a, b) => b.hits - a.hits);

        // If no real data yet, show some placeholders so it's not empty initially
        if (mapped.length === 0) {
          setUsageData([
            { name: 'Papers', hits: 0 },
            { name: 'Rides', hits: 0 },
            { name: 'Books', hits: 0 }
          ]);
        } else {
          setUsageData(mapped);
        }
      } catch (err) {
        console.error("Failed to fetch analytics", err);
      } finally {
        setLoading(false);
      }
    };
    fetchStats();
  }, []);

  const summaryItems = [
    { name: 'Rides',   value: stats.rides,   color: '#3b82f6' },
    { name: 'Papers',  value: stats.papers,  color: '#8b5cf6' },
    { name: 'Books',   value: stats.books,   color: '#f59e0b' },
    { name: 'Users',   value: stats.users,   color: '#10b981' },
  ];

  return (
    <div className="admin-report-container">
      <div className="report-main-grid single-col">
        
        {/* Feature Usage Bar Chart - Now Full Width */}
        <div className="report-card feature-usage-card glass-card">
          <div className="card-header">
            <h4>Most Used Features (Real-Time)</h4>
            <span className="badge positive">Last 30 Days</span>
          </div>
          <div className="chart-wrapper">
            <ResponsiveContainer width="100%" height={280}>
              <BarChart data={usageData} margin={{ top: 20, right: 30, left: 0, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="rgba(255,255,255,0.05)" />
                <XAxis 
                  dataKey="name" 
                  axisLine={false} 
                  tickLine={false} 
                  tick={{fill: 'rgba(255,255,255,0.7)', fontSize: 12}} 
                />
                <YAxis axisLine={false} tickLine={false} tick={{fill: 'rgba(255,255,255,0.3)', fontSize: 10}} />
                <Tooltip 
                  cursor={{fill: 'rgba(255,255,255,0.05)'}}
                  contentStyle={{ backgroundColor: '#1e293b', border: 'none', borderRadius: '8px', color: '#fff' }}
                />
                <Bar 
                  dataKey="hits" 
                  fill="#3b82f6" 
                  radius={[4, 4, 0, 0]}
                  animationDuration={1500}
                  barSize={60}
                >
                  {usageData.map((entry, index) => (
                    <Cell 
                      key={`cell-${index}`} 
                      fill={index === 0 ? '#60a5fa' : '#3b82f6'} 
                      fillOpacity={Math.max(0.4, 1 - (index * 0.15))} 
                    />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
          {loading && <div className="loading-overlay">Syncing logs...</div>}
        </div>

      </div>

      <div className="quick-summary-row">
        {summaryItems.map((item) => (
          <div key={item.name} className="summary-pill glass-card">
            <div className="pill-info">
              <span className="pill-value">{item.value}</span>
              <span className="pill-label">{item.name}</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default AdminReportChart;
