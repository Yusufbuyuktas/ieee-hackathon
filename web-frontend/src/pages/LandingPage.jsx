import React from 'react';
import { 
  Waves, 
  Smartphone, 
  ShieldCheck, 
  Stethoscope, 
  Activity, 
  ArrowRight, 
  Cpu, 
  FileJson, 
  Database, 
  ChevronDown,
  Layers,
  Sparkles,
  MapPin,
  CheckCircle2,
  Binary
} from 'lucide-react';

export default function LandingPage({ onLoginClick }) {
  const scrollToWorkflow = () => {
    document.getElementById('system-architecture')?.scrollIntoView({ behavior: 'smooth' });
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col font-sans selection:bg-cyan-500/30 overflow-x-hidden">
      
      {/* Background Ambient Glows */}
      <div className="fixed inset-0 pointer-events-none overflow-hidden z-0">
        <div className="absolute -top-40 left-1/2 -translate-x-1/2 w-[700px] h-[450px] bg-cyan-500/10 rounded-full blur-[128px] animate-pulse" />
        <div className="absolute top-[40%] -left-32 w-[500px] h-[500px] bg-blue-600/10 rounded-full blur-[140px]" />
        <div className="absolute top-[65%] -right-32 w-[500px] h-[500px] bg-emerald-500/10 rounded-full blur-[140px]" />
      </div>

      {/* Top Navigation */}
      <nav className="border-b border-slate-800/80 bg-slate-900/60 backdrop-blur-md sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 rounded-xl bg-cyan-500/10 border border-cyan-500/30 flex items-center justify-center text-cyan-400 shadow-inner shadow-cyan-500/20">
              <Waves className="w-5 h-5 animate-pulse" />
            </div>
            <div>
              <div className="flex items-center space-x-2">
                <span className="font-bold text-lg text-white tracking-tight">RiverGuard</span>
                <span className="bg-cyan-500/10 text-cyan-400 text-[10px] px-2 py-0.5 rounded-full border border-cyan-500/20 font-mono tracking-wide">
                  ONE HEALTH
                </span>
              </div>
              <p className="text-[10px] text-slate-400 hidden xs:block font-mono">Ergene Basin Surveillance Platform</p>
            </div>
          </div>

          <div className="flex items-center space-x-3">
            <button
              onClick={scrollToWorkflow}
              className="hidden sm:inline-flex text-xs font-medium text-slate-400 hover:text-cyan-300 transition-colors px-3 py-1.5"
            >
              How It Works
            </button>
            <button
              onClick={onLoginClick}
              className="flex items-center space-x-2 bg-gradient-to-r from-slate-800 to-slate-900 hover:from-slate-700 hover:to-slate-800 text-white border border-slate-700 hover:border-cyan-500/40 px-4 py-2 rounded-xl text-xs font-semibold transition-all shadow-sm group"
            >
              <span>Authorized Portal</span>
              <ArrowRight className="w-3.5 h-3.5 text-cyan-400 group-hover:translate-x-0.5 transition-transform" />
            </button>
          </div>
        </div>
      </nav>

      {/* HERO SECTION */}
      <section className="relative z-10 pt-16 pb-20 md:pt-24 md:pb-28 text-center px-4 sm:px-6 lg:px-8">
        <div className="max-w-5xl mx-auto">
          
          <div className="inline-flex items-center space-x-2 bg-cyan-500/10 border border-cyan-500/30 px-3.5 py-1.5 rounded-full text-xs text-cyan-300 font-medium mb-8 backdrop-blur-sm shadow-sm shadow-cyan-950">
            <Activity className="w-3.5 h-3.5 text-cyan-400 animate-pulse" />
            <span>Transboundary Watershed Telemetry & Public Health Integration</span>
          </div>

          <h1 className="text-4xl sm:text-6xl md:text-7xl font-extrabold text-white tracking-tight max-w-4xl mx-auto leading-[1.1] sm:leading-[1.1]">
            From River Chemistry <br />
            to <span className="bg-gradient-to-r from-cyan-400 via-teal-300 to-blue-500 bg-clip-text text-transparent">Clinical Diagnosis</span>
          </h1>

          <p className="mt-6 text-base sm:text-lg text-slate-300 max-w-2xl mx-auto leading-relaxed">
            Bridging heavy metal hydrological monitoring, crowdsourced mobile reports, 
            and computer vision moderation directly into <span className="text-cyan-300 font-medium">HL7 FHIR Clinical Decision Support</span>.
          </p>

          {/* Action CTAs */}
          <div className="mt-10 flex flex-col sm:flex-row items-center justify-center gap-4">
            <a
              href="#mobile-download"
              onClick={(e) => {
                e.preventDefault();
                alert("RiverGuard Citizen Mobile Client (Android APK) is packaging for field deployment.");
              }}
              className="w-full sm:w-auto flex items-center justify-center space-x-2.5 bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-semibold px-6 py-3.5 rounded-xl text-sm transition-all shadow-lg shadow-cyan-500/25 hover:shadow-cyan-400/35 cursor-pointer group"
            >
              <Smartphone className="w-4 h-4 group-hover:rotate-6 transition-transform" />
              <span>Get Mobile Field App</span>
            </a>

            <button
              onClick={onLoginClick}
              className="w-full sm:w-auto flex items-center justify-center space-x-2 bg-slate-900/90 hover:bg-slate-800 text-slate-200 border border-slate-700 hover:border-slate-600 px-6 py-3.5 rounded-xl text-sm font-semibold transition-all shadow-md shadow-slate-950"
            >
              <span>Clinician & Staff Sign In</span>
            </button>
          </div>

          {/* Scroll Down Trigger */}
          <div className="mt-16 flex flex-col items-center justify-center">
            <button 
              onClick={scrollToWorkflow}
              className="text-xs text-slate-500 hover:text-cyan-400 flex flex-col items-center gap-1 transition-colors group"
            >
              <span>Explore Data Pipeline</span>
              <ChevronDown className="w-4 h-4 animate-bounce group-hover:text-cyan-400" />
            </button>
          </div>

        </div>
      </section>

      {/* QUICK BENCHMARK STATS */}
      <section className="relative z-10 border-y border-slate-800/80 bg-slate-900/40 backdrop-blur-sm py-6">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-center divide-y md:divide-y-0 md:divide-x divide-slate-800">
            <div className="p-2">
              <div className="text-xl sm:text-2xl font-bold font-mono text-cyan-400">9 Metals</div>
              <div className="text-[11px] text-slate-400 uppercase tracking-wider mt-1">ICP-MS Tracked Elements</div>
            </div>
            <div className="p-2">
              <div className="text-xl sm:text-2xl font-bold font-mono text-emerald-400">WHO & EPA</div>
              <div className="text-[11px] text-slate-400 uppercase tracking-wider mt-1">Regulatory Thresholds</div>
            </div>
            <div className="p-2">
              <div className="text-xl sm:text-2xl font-bold font-mono text-indigo-400">HL7 FHIR R4</div>
              <div className="text-[11px] text-slate-400 uppercase tracking-wider mt-1">Hospital Interoperability</div>
            </div>
            <div className="p-2">
              <div className="text-xl sm:text-2xl font-bold font-mono text-blue-400">Vision AI</div>
              <div className="text-[11px] text-slate-400 uppercase tracking-wider mt-1">Real-time Anomaly Validation</div>
            </div>
          </div>
        </div>
      </section>

      {/* SYSTEM ARCHITECTURE & DATA FLOW PIPELINE */}
      <section id="system-architecture" className="relative z-10 py-20 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto w-full">
        <div className="text-center max-w-3xl mx-auto mb-16">
          <div className="inline-flex items-center space-x-1.5 text-xs font-mono text-cyan-400 bg-cyan-950/60 border border-cyan-800/60 px-3 py-1 rounded-full mb-3">
            <Layers className="w-3.5 h-3.5" />
            <span>END-TO-END PIPELINE</span>
          </div>
          <h2 className="text-2xl sm:text-4xl font-extrabold text-white tracking-tight">
            How Data Flows Through RiverGuard
          </h2>
          <p className="mt-3 text-xs sm:text-sm text-slate-400">
            A continuous loop connecting real-world watershed anomalies to clinical intervention desks in 4 stages.
          </p>
        </div>

        {/* Interactive Flow Diagram */}
        <div className="relative">
          {/* Flow Connector Line (Desktop) */}
          <div className="hidden lg:block absolute top-1/2 left-8 right-8 h-0.5 -translate-y-8 bg-gradient-to-r from-cyan-500/20 via-blue-500/40 via-indigo-500/40 to-emerald-500/30 z-0" />

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 relative z-10">
            
            {/* Step 1: Ingestion */}
            <div className="bg-slate-900/90 border border-slate-800 hover:border-cyan-500/40 rounded-2xl p-6 transition-all shadow-lg hover:shadow-cyan-500/5 group flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between mb-4">
                  <div className="w-12 h-12 rounded-xl bg-cyan-500/10 border border-cyan-500/30 flex items-center justify-center text-cyan-400 group-hover:scale-105 transition-transform">
                    <Database className="w-6 h-6" />
                  </div>
                  <span className="text-[10px] font-mono font-bold bg-slate-800 text-slate-400 px-2 py-0.5 rounded-full border border-slate-700">
                    STAGE 01
                  </span>
                </div>
                <h3 className="text-sm font-bold text-white mb-1.5 flex items-center gap-1.5">
                  Multi-Source Ingestion
                </h3>
                <p className="text-xs text-slate-400 leading-relaxed mb-4">
                  Continuous heavy metal telemetry (As, Cr, Pb, Cd) combined with georeferenced field photos captured by citizens via Android app.
                </p>
              </div>

              <div className="pt-3 border-t border-slate-800/80 flex items-center justify-between text-[11px] font-mono text-cyan-400">
                <span>IoT Nodes & Mobile</span>
                <Binary className="w-3.5 h-3.5" />
              </div>
            </div>

            {/* Step 2: AI Moderation */}
            <div className="bg-slate-900/90 border border-slate-800 hover:border-blue-500/40 rounded-2xl p-6 transition-all shadow-lg hover:shadow-blue-500/5 group flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between mb-4">
                  <div className="w-12 h-12 rounded-xl bg-blue-500/10 border border-blue-500/30 flex items-center justify-center text-blue-400 group-hover:scale-105 transition-transform">
                    <Cpu className="w-6 h-6" />
                  </div>
                  <span className="text-[10px] font-mono font-bold bg-slate-800 text-slate-400 px-2 py-0.5 rounded-full border border-slate-700">
                    STAGE 02
                  </span>
                </div>
                <h3 className="text-sm font-bold text-white mb-1.5 flex items-center gap-1.5">
                  Computer Vision AI
                </h3>
                <p className="text-xs text-slate-400 leading-relaxed mb-4">
                  High-throughput vision models classify turbidity, chemical odors, foam, and fish mortality, calculating determinative confidence scores.
                </p>
              </div>

              <div className="pt-3 border-t border-slate-800/80 flex items-center justify-between text-[11px] font-mono text-blue-400">
                <span>Gemini Vision Engine</span>
                <Sparkles className="w-3.5 h-3.5" />
              </div>
            </div>

            {/* Step 3: HL7 FHIR Standard */}
            <div className="bg-slate-900/90 border border-slate-800 hover:border-indigo-500/40 rounded-2xl p-6 transition-all shadow-lg hover:shadow-indigo-500/5 group flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between mb-4">
                  <div className="w-12 h-12 rounded-xl bg-indigo-500/10 border border-indigo-500/30 flex items-center justify-center text-indigo-400 group-hover:scale-105 transition-transform">
                    <FileJson className="w-6 h-6" />
                  </div>
                  <span className="text-[10px] font-mono font-bold bg-slate-800 text-slate-400 px-2 py-0.5 rounded-full border border-slate-700">
                    STAGE 03
                  </span>
                </div>
                <h3 className="text-sm font-bold text-white mb-1.5 flex items-center gap-1.5">
                  Clinical FHIR R4 Bridge
                </h3>
                <p className="text-xs text-slate-400 leading-relaxed mb-4">
                  Telemetry is mapped into standard LOINC-coded <code className="text-indigo-300 font-mono text-[11px]">Observation</code> resources and USEPA toxicological risk indices.
                </p>
              </div>

              <div className="pt-3 border-t border-slate-800/80 flex items-center justify-between text-[11px] font-mono text-indigo-400">
                <span>HAPI FHIR JPA</span>
                <Activity className="w-3.5 h-3.5" />
              </div>
            </div>

            {/* Step 4: Decision Support */}
            <div className="bg-slate-900/90 border border-slate-800 hover:border-emerald-500/40 rounded-2xl p-6 transition-all shadow-lg hover:shadow-emerald-500/5 group flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between mb-4">
                  <div className="w-12 h-12 rounded-xl bg-emerald-500/10 border border-emerald-500/30 flex items-center justify-center text-emerald-400 group-hover:scale-105 transition-transform">
                    <ShieldCheck className="w-6 h-6" />
                  </div>
                  <span className="text-[10px] font-mono font-bold bg-slate-800 text-slate-400 px-2 py-0.5 rounded-full border border-slate-700">
                    STAGE 04
                  </span>
                </div>
                <h3 className="text-sm font-bold text-white mb-1.5 flex items-center gap-1.5">
                  Role-Based Action Desks
                </h3>
                <p className="text-xs text-slate-400 leading-relaxed mb-4">
                  Municipal officers verify anomalies and dispatch field teams; physicians evaluate heavy metal bioaccumulation warnings during patient intake.
                </p>
              </div>

              <div className="pt-3 border-t border-slate-800/80 flex items-center justify-between text-[11px] font-mono text-emerald-400">
                <span>Live Action Dashboards</span>
                <CheckCircle2 className="w-3.5 h-3.5" />
              </div>
            </div>

          </div>
        </div>
      </section>

      {/* CORE OPERATIONAL PILLARS */}
      <section className="py-16 border-t border-slate-800/80 bg-slate-900/30 relative z-10">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center max-w-2xl mx-auto mb-12">
            <h2 className="text-xl sm:text-3xl font-extrabold text-white">Three Core Pillars of RiverGuard</h2>
            <p className="text-xs sm:text-sm text-slate-400 mt-2">
              Integrated capabilities engineered to break silos between environmental surveillance and regional clinics.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            
            {/* Pillar 1 */}
            <div className="p-6 rounded-2xl bg-slate-900/80 border border-slate-800 hover:border-slate-700 transition-all">
              <div className="w-10 h-10 rounded-xl bg-cyan-500/10 border border-cyan-500/30 flex items-center justify-center text-cyan-400 mb-4">
                <Activity className="w-5 h-5" />
              </div>
              <h3 className="text-base font-bold text-white mb-2">GIS Telemetry & Regulatory Limits</h3>
              <p className="text-xs text-slate-400 leading-relaxed">
                Live catchment geospatial mapping with automated threshold violation triggers (WHO, Turkish Standards TS-2627, and EPA) across surface and sediment media.
              </p>
            </div>

            {/* Pillar 2 */}
            <div className="p-6 rounded-2xl bg-slate-900/80 border border-slate-800 hover:border-slate-700 transition-all">
              <div className="w-10 h-10 rounded-xl bg-blue-500/10 border border-blue-500/30 flex items-center justify-center text-blue-400 mb-4">
                <ShieldCheck className="w-5 h-5" />
              </div>
              <h3 className="text-base font-bold text-white mb-2">Crowdsourced Environmental Watch</h3>
              <p className="text-xs text-slate-400 leading-relaxed">
                Empowers local residents to report localized water pollution, foam outbreaks, and fish mortality, triaged automatically into municipal response queues.
              </p>
            </div>

            {/* Pillar 3 */}
            <div className="p-6 rounded-2xl bg-slate-900/80 border border-slate-800 hover:border-slate-700 transition-all">
              <div className="w-10 h-10 rounded-xl bg-rose-500/10 border border-rose-500/30 flex items-center justify-center text-rose-400 mb-4">
                <Stethoscope className="w-5 h-5" />
              </div>
              <h3 className="text-base font-bold text-white mb-2">Clinical Decision Support (CDS)</h3>
              <p className="text-xs text-slate-400 leading-relaxed">
                Correlates patient symptom clusters with heavy metal exposure indices (Carcinogenic Risk & Hazard Index) to assist doctors during acute diagnosis.
              </p>
            </div>

          </div>
        </div>
      </section>

      {/* FOOTER */}
      <footer className="mt-auto border-t border-slate-800/80 py-8 text-center text-xs text-slate-500 bg-slate-950 relative z-10">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col sm:flex-row items-center justify-between gap-4">
          <div className="flex items-center space-x-2">
            <Waves className="w-4 h-4 text-cyan-400" />
            <span className="font-semibold text-slate-300">RiverGuard Platform</span>
            <span>• One Health Hackathon 2026</span>
          </div>
          <p className="text-slate-500 text-[11px]">
            Engineered for Ergene Catchment Environmental & Epidemiological Protection
          </p>
        </div>
      </footer>

    </div>
  );
}