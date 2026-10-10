// src/index.tsx
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import './index.css'; // Optional global styles
import "bootstrap/dist/css/bootstrap.min.css"




const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);
root.render(
  
    <App />
  
);