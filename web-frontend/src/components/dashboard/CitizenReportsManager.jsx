import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { updateCitizenReportStatus } from '../../services/apiService';
import { 
  ShieldAlert, 
  CheckCircle2, 
  XCircle, 
  Clock, 
  Cpu, 
  MapPin, 
  ExternalLink,
  Filter,
  Loader2,
  AlertTriangle
} from 'lucide-react';

export default function CitizenReportsManager({ reports = [], onReportsUpdate }) {
  const { user } = useAuth();
  const [filter, setFilter] = useState('ALL'); // 'ALL' | 'INCELEMEDE' | 'ONAYLANDI' | 'TUTARSIZ'
  const [updatingId, setUpdatingId] = useState(null);
  const [actionError, setActionError] = useState(null);

  // Operational status transition actions restricted to municipality personnel[cite: 6]
  const canManage = user?.role === 'MUNICIPALITY_STAFF';

  // Filter evaluation logic
  const filteredReports = reports.filter((report) => {
    const status = report.ai_validation_status || report.status || 'INCELEMEDE';
    if (filter === 'ALL') return true;
    return status === filter;
  });

  // Tally counters
  const counts = {
    ALL: reports.length,
    INCELEMEDE: reports.filter(r => (r.ai_validation_status || r.status) === 'INCELEMEDE').length,
    ONAYLANDI: reports.filter(r => (r.ai_validation_status || r.status) === 'ONAYLANDI').length,
    TUTARSIZ: reports.filter(r => (r.ai_validation_status || r.status) === 'TUTARSIZ').length,
  };

  // Status transition handler with optimistic local state synchronization[cite: 6]
  const handleStatusChange = async (reportId, newStatus) => {
    setUpdatingId(reportId);
    setActionError(null);

    try {
      // Backend PATCH request: /api/citizen-reports/{id}/status[cite: 6]
      await updateCitizenReportStatus(reportId, newStatus);

      if (onReportsUpdate) {
        onReportsUpdate(prev => 
          prev.map(r => r.id === reportId ? { 
            ...r, 
            ai_validation_status: newStatus, 
            status: newStatus,
            ai_verification: {
              ...r.ai_verification,
              verified: newStatus === 'ONAYLANDI'
            }
          } : r)
        );
      }
    } catch (err) {
      console.error("Status update execution error:", err);
      setActionError(`Failed to update Report #${reportId}. Please verify your active session permissions.`);
    } finally {
      setUpdatingId(null);
    }
  };

  // Status badge visual renderer
  const getStatusBadge = (status) => {
    switch (status) {
      case 'ONAYLANDI':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[11px] font-semibold bg-emerald-500/10 text-emerald-400 border border-emerald-500/30">
            <CheckCircle2 className="w-3 h-3" /> Approved
          </span>
        );
      case 'TUTARSIZ':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[11px] font-semibold bg-rose-500/10 text-rose-400 border border-rose-500/30">
            <XCircle className="w-3 h-3" /> Inconsistent
          </span>
        );
      default: // INCELEMEDE
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[11px] font-semibold bg-amber-500/10 text-amber-400 border border-amber-500/30">
            <Clock className="w-3 h-3" /> Under Review
          </span>
        );
    }
  };

  return (
    <div className="bg-slate-900/80 border border-slate-800 rounded-2xl p-5 sm:p-6 space-y-6">
      
      {/* Header and Filter Action Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-800">
        <div>
          <div className="flex items-center space-x-2">
            <ShieldAlert className="w-5 h-5 text-cyan-400" />
            <h3 className="text-base font-bold text-white tracking-tight">
              Citizen Science Field Operations Desk
            </h3>
            <span className="bg-cyan-500/10 text-cyan-400 border border-cyan-500/30 text-[10px] px-2 py-0.5 rounded font-mono">
              Municipal Coordination
            </span>
          </div>
          <p className="text-xs text-slate-400 mt-1">
            Crowdsourced environmental anomaly reports and computer vision moderation queue
          </p>
        </div>

        {/* Status Filter Toggle Chips */}
        <div className="flex flex-wrap items-center gap-1.5 bg-slate-950 p-1 rounded-xl border border-slate-800">
          <button
            onClick={() => setFilter('ALL')}
            className={`px-3 py-1 rounded-lg text-xs font-medium transition-colors ${
              filter === 'ALL'
                ? 'bg-cyan-500 text-slate-950 font-semibold'
                : 'text-slate-400 hover:text-white'
            }`}
          >
            All ({counts.ALL})
          </button>
          <button
            onClick={() => setFilter('INCELEMEDE')}
            className={`px-3 py-1 rounded-lg text-xs font-medium transition-colors ${
              filter === 'INCELEMEDE'
                ? 'bg-amber-500 text-slate-950 font-semibold'
                : 'text-slate-400 hover:text-amber-300'
            }`}
          >
            Under Review ({counts.INCELEMEDE})
          </button>
          <button
            onClick={() => setFilter('ONAYLANDI')}
            className={`px-3 py-1 rounded-lg text-xs font-medium transition-colors ${
              filter === 'ONAYLANDI'
                ? 'bg-emerald-500 text-slate-950 font-semibold'
                : 'text-slate-400 hover:text-emerald-300'
            }`}
          >
            Approved ({counts.ONAYLANDI})
          </button>
          <button
            onClick={() => setFilter('TUTARSIZ')}
            className={`px-3 py-1 rounded-lg text-xs font-medium transition-colors ${
              filter === 'TUTARSIZ'
                ? 'bg-rose-500 text-slate-950 font-semibold'
                : 'text-slate-400 hover:text-rose-300'
            }`}
          >
            Inconsistent ({counts.TUTARSIZ})
          </button>
        </div>
      </div>

      {actionError && (
        <div className="p-3 bg-rose-950/60 border border-rose-500/40 rounded-xl text-rose-300 text-xs flex items-center space-x-2">
          <AlertTriangle className="w-4 h-4 text-rose-400 shrink-0" />
          <span>{actionError}</span>
        </div>
      )}

      {/* Report Cards Grid */}
      {filteredReports.length === 0 ? (
        <div className="py-12 text-center text-slate-500 text-xs font-mono">
          No citizen observation reports match the active filter criteria.
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {filteredReports.map((report) => {
            const currentStatus = report.ai_validation_status || report.status || 'INCELEMEDE';
            const isUpdating = updatingId === report.id;

            return (
              <div 
                key={report.id}
                className="bg-slate-950/60 border border-slate-800 rounded-xl p-4 flex flex-col justify-between hover:border-slate-700 transition-all space-y-4"
              >
                {/* Photo Thumbnail and Primary Metadata */}
                <div className="flex gap-4">
                  <div className="w-24 h-24 sm:w-28 sm:h-28 rounded-xl bg-slate-900 border border-slate-800 overflow-hidden shrink-0 relative group">
                    {report.photo_url ? (
                      <img 
                        src={report.photo_url} 
                        alt="Field Observation"
                        className="w-full h-full object-cover transition-transform group-hover:scale-105"
                        onError={(e) => {
                          e.target.onerror = null;
                          e.target.src = 'https://images.unsplash.com/photo-1617155093730-a8bf47be792d?auto=format&fit=crop&w=400&q=80';
                        }}
                      />
                    ) : (
                      <div className="w-full h-full flex items-center justify-center text-[10px] text-slate-600 text-center p-2">
                        No Photo Available
                      </div>
                    )}
                    {report.photo_url && (
                      <a 
                        href={report.photo_url} 
                        target="_blank" 
                        rel="noreferrer"
                        className="absolute inset-0 bg-slate-950/60 opacity-0 group-hover:opacity-100 flex items-center justify-center text-white transition-opacity"
                      >
                        <ExternalLink className="w-4 h-4" />
                      </a>
                    )}
                  </div>

                  <div className="flex-1 min-w-0 space-y-1.5">
                    <div className="flex items-start justify-between gap-2">
                      <span className="text-xs font-bold text-white truncate">
                        {report.category_label || report.category}
                      </span>
                      {getStatusBadge(currentStatus)}
                    </div>

                    <div className="flex items-center text-[11px] text-slate-400 space-x-1">
                      <MapPin className="w-3 h-3 text-cyan-400 shrink-0" />
                      <span className="truncate">{report.location_name}</span>
                    </div>

                    <p className="text-xs text-slate-300 line-clamp-2 italic bg-slate-900/50 p-2 rounded-lg border border-slate-800/80">
                      "{report.note || 'No notes provided by observer.'}"
                    </p>

                    <div className="text-[10px] text-slate-500 font-mono">
                      {report.timestamp ? new Date(report.timestamp).toLocaleString('en-US') : 'Timestamp unavailable'}
                    </div>
                  </div>
                </div>

                {/* AI Computer Vision Moderation Assessment Card */}
                <div className="bg-slate-900/80 border border-slate-800/90 rounded-xl p-3 space-y-2">
                  <div className="flex items-center justify-between text-[11px]">
                    <div className="flex items-center space-x-1.5 text-cyan-400 font-medium">
                      <Cpu className="w-3.5 h-3.5" />
                      <span>AI Computer Vision Moderation</span>
                    </div>

                    {/* AI Model Architecture Badge[cite: 6] */}
                    {(report.ai_verification?.model || report.ai_model) && (
                      <span className="text-[10px] text-slate-400 font-mono bg-slate-800 px-2 py-0.5 rounded border border-slate-700">
                        {report.ai_verification?.model || report.ai_model}
                      </span>
                    )}

                    <span className="text-[10px] font-mono text-slate-400">
                      Confidence: {((report.ai_verification?.confidence ?? 0.85) * 100).toFixed(0)}%
                    </span>
                  </div>

                  <p className="text-xs text-slate-300 leading-relaxed">
                    {report.ai_explanation || report.ai_verification?.feedback || "Computer vision anomaly validation completed."}
                  </p>
                </div>

                {/* Municipality Staff Operational Verification Controls (Restricted to INCELEMEDE status)[cite: 5, 6] */}
                {canManage && currentStatus === 'INCELEMEDE' && (
                  <div className="pt-2 border-t border-slate-800/80 flex items-center justify-end gap-2">
                    <button
                      onClick={() => handleStatusChange(report.id, 'TUTARSIZ')}
                      disabled={isUpdating}
                      className="flex items-center space-x-1 text-xs px-3 py-1.5 rounded-lg bg-rose-500/10 hover:bg-rose-500/20 text-rose-300 border border-rose-500/30 transition-colors disabled:opacity-50"
                    >
                      <XCircle className="w-3.5 h-3.5 text-rose-400" />
                      <span>Mark Inconsistent</span>
                    </button>

                    <button
                      onClick={() => handleStatusChange(report.id, 'ONAYLANDI')}
                      disabled={isUpdating}
                      className="flex items-center space-x-1 text-xs px-3 py-1.5 rounded-lg bg-emerald-500/10 hover:bg-emerald-500/20 text-emerald-300 border border-emerald-500/30 transition-colors disabled:opacity-50"
                    >
                      {isUpdating ? (
                        <Loader2 className="w-3.5 h-3.5 animate-spin text-emerald-400" />
                      ) : (
                        <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400" />
                      )}
                      <span>Approve Report</span>
                    </button>
                  </div>
                )}
              </div>
            );
          })}
        </div>
      )}

    </div>
  );
}