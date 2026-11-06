import{o as e}from"./chunk-zsgVPwQN.js";import{n as t}from"./react-dom-C2Cg8Z4w.js";import{n,t as r}from"./jsx-runtime-C3SrMh-X.js";import{J as i,f as a,h as o}from"./fi-m_7rYGJu.js";/* empty css                */var s=e(n(),1),c=e(t(),1),l=r();function u({pct:e,angularNode:t,rulesNode:n}){let r=parseFloat(e)>0,s=(0,c.useRef)(null);return(0,c.useEffect)(()=>{s.current&&(n&&s.current.appendChild(n),t&&s.current.appendChild(t))},[t,n]),(0,l.jsxs)(`div`,{className:`text-[13px] text-slate-800`,style:{boxSizing:`border-box`,fontFamily:`-apple-system,BlinkMacSystemFont,'Segoe UI','Inter',Roboto,sans-serif`},children:[(0,l.jsxs)(`div`,{className:`flex items-center justify-between flex-wrap gap-3 mb-6 pb-4`,style:{borderBottom:`1px solid var(--apx-c-e2e8f0)`},children:[(0,l.jsxs)(`h2`,{className:`text-[18px] font-bold text-slate-900 tracking-tight m-0`,children:[`Course `,(0,l.jsx)(`span`,{style:{background:`linear-gradient(135deg, var(--apx-c-e11d48), var(--apx-c-f43f5e))`,WebkitBackgroundClip:`text`,WebkitTextFillColor:`transparent`,backgroundClip:`text`},children:`Drop`}),` Application`]}),(0,l.jsxs)(`div`,{style:{display:`flex`,alignItems:`center`,gap:`8px`,background:r?`var(--apx-c-f0fdf4)`:`var(--apx-c-fff1f2)`,borderRadius:`10px`,padding:`8px 16px`},children:[(0,l.jsx)(o,{style:{fontSize:`15px`,color:r?`var(--apx-c-15803d)`:`var(--apx-c-be123c)`}}),(0,l.jsx)(`span`,{style:{fontSize:`11px`,fontWeight:700,color:r?`var(--apx-c-15803d)`:`var(--apx-c-be123c)`,textTransform:`uppercase`,letterSpacing:`0.05em`},children:`Refund`}),(0,l.jsx)(`span`,{style:{fontSize:`18px`,fontWeight:800,color:r?`var(--apx-c-166534)`:`var(--apx-c-991b1b)`,fontFamily:`ui-monospace, monospace`},children:e}),(0,l.jsxs)(`span`,{style:{display:`inline-flex`,alignItems:`center`,gap:`4px`,fontSize:`10px`,fontWeight:700,padding:`2px 8px`,borderRadius:`9999px`,background:r?`var(--apx-c-dcfce7)`:`var(--apx-c-ffe4e6)`,color:r?`var(--apx-c-15803d)`:`var(--apx-c-be123c)`},children:[r?(0,l.jsx)(a,{style:{fontSize:`11px`}}):(0,l.jsx)(i,{style:{fontSize:`11px`}}),r?`Eligible`:`Not Eligible`]})]})]}),(0,l.jsx)(`div`,{ref:s})]})}(function(){window.__aiubDropMounted||window.location.href.includes(`/Student/Adrop/DropApplication`)&&chrome.storage.sync.get({extensionEnabled:!0,featureToggles:{}},e=>{if(!e.extensionEnabled||e.featureToggles?.dropApplication===!1)return;function t(){if(!document.querySelectorAll(`[ng-controller="DropApplicationController2"] [ng-repeat].ng-scope`).length){setTimeout(t,300);return}if(window.__aiubDropMounted)return;window.__aiubDropMounted=!0;let e=document.createElement(`style`);e.id=`aiub-drop-style`,e.textContent=`
        .portal-body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Inter', Roboto, sans-serif; }
        .alert.alert-warning { display: none !important; }

        /* Rules toggle button */
        [data-target="#Rules"] .label.label-info {
          font-size: 12px !important; padding: 6px 14px !important; border-radius: 6px !important;
          background: var(--apx-c-dbeafe) !important; color: var(--apx-t-0284c7) !important; border: 1px solid var(--apx-c-bfdbfe) !important;
          font-weight: 600 !important; cursor: pointer !important;
        }

        /* Rules table */
        #Rules .table { border-collapse: collapse !important; border-radius: 8px; overflow: hidden; border: 1px solid var(--apx-c-e2e8f0) !important; background: var(--apx-c-ffffff) !important; }
        #Rules .table th { background: linear-gradient(to right, var(--apx-c-f8fafc), var(--apx-c-f1f5f9)) !important; font-size: 11px !important; text-transform: uppercase; letter-spacing: .6px; color: var(--apx-t-64748b) !important; font-weight: 700 !important; border-bottom: 1px solid var(--apx-c-e2e8f0) !important; padding: 11px 14px !important; }
        #Rules .table td { font-size: 12px !important; padding: 9px 14px !important; color: var(--apx-t-475569) !important; border-color: var(--apx-c-f1f5f9) !important; }
        #Rules .table tbody tr:hover { background: var(--apx-c-f8fafc) !important; }

        /* COURSE CARDS */
        [ng-controller="DropApplicationController2"] .ng-scope > [ng-repeat] { display: none !important; }

        [ng-controller="DropApplicationController2"] [ng-repeat].ng-scope {
          background: linear-gradient(135deg, var(--apx-c-f8fbff) 0%, var(--apx-c-eff6ff) 50%, var(--apx-c-dbeafe) 100%) !important;
          border: 1px solid var(--apx-c-bfdbfe) !important;
          border-radius: 16px !important;
          margin-bottom: 16px !important;
          padding: 0 !important;
          overflow: hidden !important;
          transition: box-shadow 0.2s ease, border-color 0.2s ease !important;
          box-shadow: 0 1px 4px 0 rgba(0,0,0,0.06) !important;
        }
        [ng-controller="DropApplicationController2"] [ng-repeat].ng-scope:hover {
          box-shadow: 0 6px 20px -4px rgba(0,0,0,0.1) !important;
          border-color: var(--apx-c-bfdbfe) !important;
        }

        /* Card with error/warning highlight */
        [ng-controller="DropApplicationController2"] [ng-repeat].ng-scope:has(.col-md-2 span.ng-binding:not(.ng-hide)) {
          border-color: var(--apx-c-fecdd3) !important;
          background: linear-gradient(135deg, var(--apx-c-fff8f8) 0%, var(--apx-c-fff1f2) 50%, var(--apx-c-ffe4e6) 100%) !important;
        }

        [ng-controller="DropApplicationController2"] [ng-repeat].ng-scope > .row {
          margin: 0 !important;
          padding: 20px 24px !important;
          display: flex !important;
          align-items: center !important;
          gap: 20px !important;
        }

        [ng-controller="DropApplicationController2"] [ng-repeat] .col-md-1 {
          flex: 0 0 auto !important;
          padding: 0 !important;
          width: auto !important;
          display: flex !important;
          flex-direction: column !important;
          align-items: center !important;
          justify-content: center !important;
          gap: 6px !important;
        }
        [ng-controller="DropApplicationController2"] [ng-repeat] .col-md-1 > span[style*="steelblue"] {
          display: flex !important;
          flex-direction: column !important;
          align-items: center !important;
          justify-content: center !important;
          width: 64px !important;
          height: 64px !important;
          background: linear-gradient(135deg, var(--apx-c-eff6ff), var(--apx-c-dbeafe)) !important;
          border-radius: 14px !important;
          color: var(--apx-t-2563eb) !important;
          font-family: ui-monospace, monospace !important;
          font-size: 13px !important;
          font-weight: 800 !important;
          letter-spacing: 0.02em !important;
          text-transform: uppercase !important;
          padding: 0 !important;
          text-align: center !important;
          line-height: 1.2 !important;
        }

        [ng-controller="DropApplicationController2"] [ng-repeat] .col-md-6 {
          flex: 1 1 auto !important;
          padding: 0 !important;
          min-width: 0 !important;
        }
        [ng-controller="DropApplicationController2"] [ng-repeat] .col-md-6 > span[style*="slateblue"] {
          color: var(--apx-t-0f172a) !important;
          font-size: 15px !important;
          font-weight: 700 !important;
          line-height: 1.3 !important;
          display: block !important;
          margin-bottom: 8px !important;
        }
        [ng-controller="DropApplicationController2"] [ng-repeat].ng-scope:has(.col-md-2 span.ng-binding:not(.ng-hide)) .col-md-6 > span[style*="slateblue"] {
          text-decoration: line-through !important;
          color: var(--apx-t-94a3b8) !important;
        }

        /* Schedule tags row */
        [ng-controller="DropApplicationController2"] [ng-repeat] .col-md-6 small.ng-binding {
          display: inline-flex !important;
          align-items: center !important;
          margin: 0 6px 6px 0 !important;
          font-size: 11px !important;
          color: var(--apx-t-334155) !important;
          background: var(--apx-c-f1f5f9) !important;
          padding: 4px 10px !important;
          border-radius: 6px !important;
          font-weight: 600 !important;
          letter-spacing: 0.01em !important;
          border: 1px solid var(--apx-c-e2e8f0) !important;
        }

        /* Supervisors row */
        [ng-controller="DropApplicationController2"] [ng-repeat] .col-md-6 small.text-muted,
        [ng-controller="DropApplicationController2"] [ng-repeat] .col-md-6 small:not(.ng-binding) {
          display: block !important;
          font-size: 11px !important;
          color: var(--apx-t-64748b) !important;
          margin-top: 4px !important;
        }

        [ng-controller="DropApplicationController2"] [ng-repeat] .col-md-2 {
          flex: 0 0 auto !important;
          padding: 0 !important;
          display: flex !important;
          flex-direction: column !important;
          align-items: flex-end !important;
          gap: 10px !important;
          min-width: 200px !important;
        }

        /* Credits & Capacity pill boxes */
        [ng-controller="DropApplicationController2"] [ng-repeat] .col-md-2 small.ng-binding {
          display: inline-flex !important;
          align-items: center !important;
          gap: 6px !important;
          background: var(--apx-c-f8fafc) !important;
          border: none !important;
          border-radius: 8px !important;
          padding: 5px 12px !important;
          font-size: 11px !important;
          font-weight: 600 !important;
          color: var(--apx-t-475569) !important;
        }

        /* Error badge */
        [ng-controller="DropApplicationController2"] [ng-repeat] .col-md-2 span.ng-binding:not(.ng-hide) {
          display: inline-flex !important;
          align-items: center !important;
          background: var(--apx-c-fef2f2) !important;
          color: var(--apx-t-991b1b) !important;
          font-size: 10px !important;
          font-weight: 700 !important;
          padding: 3px 10px !important;
          border-radius: 6px !important;
          border: none !important;
        }

        /* Drop button */
        [ng-controller="DropApplicationController2"] [ng-repeat] a.btn.btn-danger {
          display: inline-flex !important;
          align-items: center !important;
          justify-content: center !important;
          gap: 6px !important;
          font-size: 13px !important;
          font-weight: 600 !important;
          background: var(--apx-c-fff1f2) !important;
          color: var(--apx-t-e11d48) !important;
          border: none !important;
          border-radius: 10px !important;
          padding: 8px 24px !important;
          width: 100% !important;
          box-shadow: none !important;
          transition: all .2s ease !important;
          cursor: pointer !important;
          text-transform: none !important;
          letter-spacing: 0 !important;
          outline: none !important;
        }
        [ng-controller="DropApplicationController2"] [ng-repeat] a.btn.btn-danger:hover {
          background: var(--apx-c-ffe4e6) !important;
          box-shadow: 0 4px 12px -2px rgba(239,68,68,0.15) !important;
        }
        [ng-controller="DropApplicationController2"] [ng-repeat] a.btn.btn-danger::before {
          content: '' !important;
          display: none !important;
        }

        /* Warning label */
        [ng-controller="DropApplicationController2"] [ng-repeat] .label.label-warning {
          background: var(--apx-c-fef3c7) !important; color: var(--apx-t-92400e) !important; border-radius: 5px !important;
          font-size: 11px !important; padding: 4px 10px !important; font-weight: 700 !important;
        }

        /* Section heading */
        [ng-controller="DropApplicationController2"] h5.text-center {
          font-size: 14px !important; font-weight: 700 !important; color: var(--apx-t-1e293b) !important; margin: 12px 0 !important;
        }

        /* Summary table */
        [ng-controller="DropApplicationController2"] .table-condensed th {
          font-size: 11px !important; text-transform: uppercase; letter-spacing: .6px; color: var(--apx-t-64748b) !important;
          background: linear-gradient(to right, var(--apx-c-f8fafc), var(--apx-c-f1f5f9)) !important; border-bottom: 1px solid var(--apx-c-e2e8f0) !important;
          padding: 10px 12px !important; font-weight: 700 !important;
        }
        [ng-controller="DropApplicationController2"] .table-condensed td {
          font-size: 12px !important; padding: 9px 12px !important; color: var(--apx-t-475569) !important; border-color: var(--apx-c-f1f5f9) !important;
        }
        [ng-controller="DropApplicationController2"] .table-condensed tbody tr:hover { background: var(--apx-c-eff6ff) !important; }
      `,document.head.appendChild(e);let n=document.querySelector(`.alert.alert-warning`),r=`0%`;if(n){let e=n.querySelector(`.label-danger b`)||n.querySelector(`.label-danger`);e&&(r=e.textContent.trim())}let i=document.querySelector(`[ng-controller="DropApplicationController2"]`),a=document.querySelector(`.portal-body > .row`),o=a||i;if(!o)return;let c=document.createElement(`div`);o.parentNode.insertBefore(c,o),(0,s.createRoot)(c).render((0,l.jsx)(u,{pct:r,angularNode:i,rulesNode:a}))}document.readyState===`loading`?document.addEventListener(`DOMContentLoaded`,t):t()})})();