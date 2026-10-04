import React, { useState } from 'react';
// Görseller assets klasöründen alınıyor
import heroImg from '../assets/hero1.jpg';
import wallpaperImg from '../assets/WALLPAPER.webp';
import pipelineImg from '../assets/pipeline.jpg'; 

import { 
  Waves, 
  Smartphone, 
  ShieldCheck, 
  Stethoscope, 
  Activity, 
  ArrowRight, 
  ChevronDown,
  Layers,
  X,
  ZoomIn,
  MoveRight
} from 'lucide-react';

export default function LandingPage({ onLoginClick }) {
  // Görsel büyütme modal durumu
  const [isImageModalOpen, setIsImageModalOpen] = useState(false);

  const scrollToWorkflow = () => {
    document.getElementById('system-architecture')?.scrollIntoView({ behavior: 'smooth' });
  };

  return (
    <div className="relative min-h-screen bg-slate-950 text-slate-100 flex flex-col font-sans selection:bg-cyan-500/30 overflow-x-hidden">
      
      {/* ========================================================================= */}
      {/* 1. ALT KISIMLAR İÇİN NET WALLPAPER */}
      {/* ========================================================================= */}
      <div className="fixed inset-0 pointer-events-none overflow-hidden z-0">
        <img 
          src={wallpaperImg} 
          alt="RiverGuard Catchment" 
          className="absolute inset-0 w-full h-full object-cover opacity-60 transition-opacity"
        />
        <div className="absolute inset-0 bg-slate-950/40" />

        <div className="absolute top-[45%] -left-32 w-[500px] h-[500px] bg-blue-600/10 rounded-full blur-[140px]" />
        <div className="absolute top-[75%] -right-32 w-[500px] h-[500px] bg-emerald-500/10 rounded-full blur-[140px]" />
      </div>

      {/* Top Navigation */}
      <nav className="border-b border-slate-800/80 bg-slate-950/80 backdrop-blur-xl sticky top-0 z-50 transition-all">
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
              className="hidden sm:inline-flex text-xs font-medium text-slate-300 hover:text-cyan-300 transition-colors px-3 py-1.5 cursor-pointer"
            >
              How It Works
            </button>
            <button
              onClick={onLoginClick}
              className="flex items-center space-x-2 bg-gradient-to-r from-slate-900 to-slate-800 hover:from-slate-800 hover:to-slate-700 text-white border border-slate-700 hover:border-cyan-500/40 px-4 py-2 rounded-xl text-xs font-semibold transition-all shadow-sm group cursor-pointer"
            >
              <span>Authorized Portal</span>
              <ArrowRight className="w-3.5 h-3.5 text-cyan-400 group-hover:translate-x-0.5 transition-transform" />
            </button>
          </div>
        </div>
      </nav>

      {/* ========================================================================= */}
      {/* 2. HERO SECTION */}
      {/* ========================================================================= */}
      <section className="relative z-10 pt-16 pb-24 md:pt-28 md:pb-36 text-center px-4 sm:px-6 lg:px-8 overflow-hidden">
        
        {/* PC Arka Plan Görseli */}
        <div className="hidden md:block absolute inset-0 pointer-events-none z-0">
          <img 
            src={heroImg} 
            alt="River Water Underwater Telemetry" 
            className="w-full h-full object-cover object-center filter brightness-90 contrast-105"
          />
          <div className="absolute inset-0 bg-slate-950/25" />
          <div className="absolute inset-0 bg-[radial-gradient(ellipse_65%_55%_at_50%_45%,_rgba(2,6,23,0.55)_0%,_transparent_100%)]" />
          <div className="absolute inset-0 bg-gradient-to-b from-slate-950/60 via-transparent to-slate-950" />
        </div>

        {/* Hero İçeriği */}
        <div className="relative z-10 max-w-5xl mx-auto">
          
          <div className="inline-flex items-center space-x-2 bg-slate-950/80 border border-cyan-500/40 px-4 py-1.5 rounded-full text-xs text-cyan-300 font-medium mb-8 backdrop-blur-md shadow-lg shadow-black/60">
            <Activity className="w-3.5 h-3.5 text-cyan-400 animate-pulse" />
            <span>Transboundary Watershed Telemetry & Public Health Integration</span>
          </div>

          <h1 className="text-4xl sm:text-6xl md:text-7xl font-extrabold text-white tracking-tight max-w-4xl mx-auto leading-[1.12] sm:leading-[1.12] drop-shadow-[0_6px_20px_rgba(0,0,0,0.95)]">
            From River Chemistry <br />
            to{' '}
            <span className="bg-gradient-to-r from-cyan-200 via-cyan-400 to-teal-300 bg-clip-text text-transparent drop-shadow-[0_4px_16px_rgba(6,182,212,0.4)]">
              Clinical Diagnosis
            </span>
          </h1>

          <p className="mt-6 text-base sm:text-lg text-slate-100 max-w-3xl mx-auto leading-relaxed drop-shadow-[0_3px_10px_rgba(0,0,0,0.95)] font-normal">
            Bridging heavy metal hydrological monitoring, crowdsourced mobile reports, 
            and computer vision moderation directly into{' '}
            <span className="text-cyan-300 font-medium bg-slate-950/60 px-2 py-0.5 rounded-md border border-cyan-500/30 inline-block backdrop-blur-xs">
              HL7 FHIR Clinical Decision Support
            </span>.
          </p>

          <div className="mt-10 flex flex-col sm:flex-row items-center justify-center gap-4">
  <a
    href="https://drive.google.com/uc?export=download&id=18dd-oHfefdbVVAIVgVjXmSSH7-U-nKzG"
    target="_blank"
    rel="noopener noreferrer"
    className="w-full sm:w-auto flex items-center justify-center space-x-2.5 bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-bold px-7 py-3.5 rounded-xl text-sm transition-all shadow-xl shadow-cyan-950 hover:shadow-cyan-400/40 cursor-pointer group"
  >
    <Smartphone className="w-4 h-4 group-hover:rotate-6 transition-transform" />
    <span>Get Mobile Field App</span>
  </a>

  <button
    onClick={onLoginClick}
    className="w-full sm:w-auto flex items-center justify-center space-x-2 bg-slate-950/90 hover:bg-slate-900 text-white border border-slate-700/80 hover:border-slate-500 px-7 py-3.5 rounded-xl text-sm font-semibold transition-all shadow-xl shadow-black/70 backdrop-blur-md cursor-pointer"
  >
    <span>Clinician & Staff Sign In</span>
  </button>
</div>

          <div className="mt-16 flex flex-col items-center justify-center">
            <button 
              onClick={scrollToWorkflow}
              className="text-xs text-slate-300 hover:text-cyan-300 flex flex-col items-center gap-1 transition-colors group cursor-pointer drop-shadow-[0_2px_4px_rgba(0,0,0,0.8)]"
            >
              <span className="font-medium">Explore Data Pipeline</span>
              <ChevronDown className="w-4 h-4 animate-bounce group-hover:text-cyan-400" />
            </button>
          </div>

        </div>
      </section>

      {/* ========================================================================= */}
      {/* QUICK BENCHMARK STATS */}
      {/* ========================================================================= */}
      <section className="relative z-10 border-y border-slate-800 bg-slate-950/85 backdrop-blur-xl py-6">
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

      {/* ========================================================================= */}
      {/* SYSTEM ARCHITECTURE & DATA FLOW PIPELINE (PANORAMİK MOBİL KAYDIRMA) */}
      {/* ========================================================================= */}
      <section id="system-architecture" className="relative z-10 py-16 md:py-24 w-full">
        
        {/* Başlık Bölümü */}
        <div className="text-center max-w-3xl mx-auto mb-8 md:mb-14 px-4 sm:px-6">
          <div className="inline-flex items-center space-x-1.5 text-xs font-mono text-cyan-400 bg-cyan-950/80 border border-cyan-800/80 px-3 py-1 rounded-full mb-3 backdrop-blur-md">
            <Layers className="w-3.5 h-3.5" />
            <span>END-TO-END PIPELINE</span>
          </div>
          <h2 className="text-2xl sm:text-4xl font-extrabold text-white tracking-tight">
            How Data Flows Through RiverGuard
          </h2>
          <p className="mt-3 text-xs sm:text-sm text-slate-300">
            A continuous loop connecting real-world watershed anomalies to clinical intervention desks in 4 stages.
          </p>
        </div>

        {/* MOBİL İÇİN ETKİLEŞİMLİ KAYDIRMA ALANI */}
        <div className="relative w-full">
          
          {/* Mobile horizontal scroll guide */}
<div className="md:hidden flex items-center justify-between px-4 mb-3">
  <span className="inline-flex items-center gap-1.5 bg-slate-900/90 border border-cyan-500/30 text-cyan-300 text-xs px-3 py-1.5 rounded-full font-mono shadow-md backdrop-blur-md animate-pulse">
    <span>Scroll right to explore stages</span>
    <MoveRight className="w-3.5 h-3.5" />
  </span>
  <span className="text-[11px] font-mono text-slate-400">
    Stage 01 → Stage 03
  </span>
</div>

          {/* Görsel Scroll Taşıyıcısı (Mobilde overflow-x-auto, Masaüstünde tam sığma) */}
          <div 
            className="w-full overflow-x-auto overflow-y-hidden scroll-smooth overscroll-x-contain pb-2 md:pb-0 [scrollbar-width:thin] [scrollbar-color:#06b6d4_#0f172a]"
            style={{ WebkitOverflowScrolling: 'touch' }}
          >
            {/* Mobilde: Ekranın %75'i yükseklik, genişlik serbest (yazılar büyük kalır)
                Masaüstünde: Normal tam genişlikli akış */}
            <div className="w-max md:w-full flex md:block">
              <img 
                src={pipelineImg} 
                alt="RiverGuard Data Pipeline Architecture" 
                onClick={() => setIsImageModalOpen(true)}
                className="h-[75vh] min-h-[500px] max-h-[660px] w-auto max-w-none md:h-auto md:w-full md:max-w-7xl md:mx-auto object-contain cursor-zoom-in select-none block"
              />
            </div>
          </div>

          {/* Mobilde sağ alt köşe Büyütme Butonu Rozeti */}
          <div className="md:hidden flex justify-end px-4 mt-2">
            <button
              onClick={() => setIsImageModalOpen(true)}
              className="text-[11px] font-mono text-slate-400 hover:text-cyan-300 flex items-center gap-1 bg-slate-900/80 px-2.5 py-1 rounded-lg border border-slate-800"
            >
              <ZoomIn className="w-3.5 h-3.5 text-cyan-400" />
              <span>Tam ekran görüntüle</span>
            </button>
          </div>

        </div>

      </section>

      {/* ========================================================================= */}
      {/* FOTOĞRAF BÜYÜTME POPUP / LIGHTBOX */}
      {/* ========================================================================= */}
      {isImageModalOpen && (
        <div 
          onClick={() => setIsImageModalOpen(false)}
          className="fixed inset-0 z-50 bg-black/95 backdrop-blur-md flex flex-col items-center justify-center p-2 sm:p-6 animate-in fade-in duration-200"
        >
          {/* Kapatma Butonu */}
          <button 
            onClick={() => setIsImageModalOpen(false)}
            className="absolute top-4 right-4 z-50 p-2.5 rounded-full bg-slate-900/90 text-white hover:text-cyan-400 border border-slate-700 backdrop-blur-md transition-all shadow-xl cursor-pointer"
            aria-label="Kapat"
          >
            <X className="w-6 h-6" />
          </button>

          {/* Tam Ekran Kaydırılabilir Görsel Konteynırı */}
          <div 
            onClick={(e) => e.stopPropagation()} 
            className="w-full h-full max-h-[92vh] flex items-center justify-center overflow-auto p-1"
          >
            <img 
              src={pipelineImg} 
              alt="RiverGuard Data Pipeline Architecture Full View" 
              className="max-w-full max-h-full object-contain rounded-lg shadow-2xl"
            />
          </div>
        </div>
      )}

      {/* ========================================================================= */}
      {/* CORE OPERATIONAL PILLARS */}
      {/* ========================================================================= */}
      <section className="py-16 border-t border-slate-800 bg-slate-950/80 backdrop-blur-md relative z-10">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center max-w-2xl mx-auto mb-12">
            <h2 className="text-xl sm:text-3xl font-extrabold text-white">Three Core Pillars of RiverGuard</h2>
            <p className="text-xs sm:text-sm text-slate-300 mt-2">
              Integrated capabilities engineered to break silos between environmental surveillance and regional clinics.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            
            {/* Pillar 1 */}
            <div className="p-6 rounded-2xl bg-slate-950/80 backdrop-blur-md border border-slate-800 hover:border-slate-700 transition-all hover:translate-y-[-2px]">
              <div className="w-10 h-10 rounded-xl bg-cyan-500/10 border border-cyan-500/30 flex items-center justify-center text-cyan-400 mb-4">
                <Activity className="w-5 h-5" />
              </div>
              <h3 className="text-base font-bold text-white mb-2">GIS Telemetry & Regulatory Limits</h3>
              <p className="text-xs text-slate-300 leading-relaxed">
                Live catchment geospatial mapping with automated threshold violation triggers (WHO, Turkish Standards TS-2627, and EPA) across surface and sediment media.
              </p>
            </div>

            {/* Pillar 2 */}
            <div className="p-6 rounded-2xl bg-slate-950/80 backdrop-blur-md border border-slate-800 hover:border-slate-700 transition-all hover:translate-y-[-2px]">
              <div className="w-10 h-10 rounded-xl bg-blue-500/10 border border-blue-500/30 flex items-center justify-center text-blue-400 mb-4">
                <ShieldCheck className="w-5 h-5" />
              </div>
              <h3 className="text-base font-bold text-white mb-2">Crowdsourced Environmental Watch</h3>
              <p className="text-xs text-slate-300 leading-relaxed">
                Empowers local residents to report localized water pollution, foam outbreaks, and fish mortality, triaged automatically into municipal response queues.
              </p>
            </div>

            {/* Pillar 3 */}
            <div className="p-6 rounded-2xl bg-slate-950/80 backdrop-blur-md border border-slate-800 hover:border-slate-700 transition-all hover:translate-y-[-2px]">
              <div className="w-10 h-10 rounded-xl bg-rose-500/10 border border-rose-500/30 flex items-center justify-center text-rose-400 mb-4">
                <Stethoscope className="w-5 h-5" />
              </div>
              <h3 className="text-base font-bold text-white mb-2">Clinical Decision Support (CDS)</h3>
              <p className="text-xs text-slate-300 leading-relaxed">
                Correlates patient symptom clusters with heavy metal exposure indices (Carcinogenic Risk & Hazard Index) to assist doctors during acute diagnosis.
              </p>
            </div>

          </div>
        </div>
      </section>

      {/* FOOTER */}
      <footer className="mt-auto border-t border-slate-800/80 py-8 text-center text-xs text-slate-400 bg-slate-950/95 backdrop-blur-md relative z-10">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col sm:flex-row items-center justify-between gap-4">
          <div className="flex items-center space-x-2">
            <Waves className="w-4 h-4 text-cyan-400" />
            <span className="font-semibold text-slate-200">RiverGuard Platform</span>
            <span>• One Health Hackathon 2026</span>
          </div>
          <p className="text-slate-400 text-[11px]">
            Engineered for Ergene Catchment Environmental & Epidemiological Protection
          </p>
        </div>
      </footer>

    </div>
  );
}