const http = require('http');
const fs = require('fs');
const path = require('path');

// Determine port: Cloud Run sets PORT (e.g., 8080). Dev server uses DEFAULT_APP_PORT (3000) or PORT.
const PORT = parseInt(process.env.PORT || process.env.DEFAULT_APP_PORT || '8080', 10);
const HOST = '0.0.0.0';

// Locate index.html across multiple possible locations (root, dist, public, build)
const possibleIndexPaths = [
  path.join(__dirname, 'index.html'),
  path.join(process.cwd(), 'index.html'),
  path.join(__dirname, 'dist', 'index.html'),
  path.join(__dirname, 'public', 'index.html'),
  path.join(__dirname, 'build', 'index.html')
];

let cachedIndexHtml = null;

function getIndexHtml() {
  for (const p of possibleIndexPaths) {
    if (fs.existsSync(p)) {
      try {
        return fs.readFileSync(p, 'utf8');
      } catch (err) {
        console.error(`Error reading ${p}:`, err);
      }
    }
  }
  return cachedIndexHtml || '<!DOCTYPE html><html><head><title>Rozana</title></head><body><h1>Rozana App Loading...</h1></body></html>';
}

// Pre-cache on startup
cachedIndexHtml = getIndexHtml();

const manifestContent = JSON.stringify({
  name: 'Rozana – روزانہ',
  short_name: 'Rozana',
  start_url: '/',
  display: 'standalone',
  background_color: '#F5F2FC',
  theme_color: '#6028E4'
});

const server = http.createServer((req, res) => {
  const urlPath = req.url.split('?')[0];

  // Cloud Run / Google health check probes
  if (urlPath === '/health' || urlPath === '/healthz' || urlPath === '/_health' || urlPath === '/ping') {
    res.writeHead(200, { 'Content-Type': 'text/plain; charset=utf-8' });
    res.end('OK');
    return;
  }

  // Favicon request
  if (urlPath === '/favicon.ico') {
    res.writeHead(204);
    res.end();
    return;
  }

  // Web manifest request
  if (urlPath === '/manifest.json') {
    res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8' });
    res.end(manifestContent);
    return;
  }

  // Robots.txt
  if (urlPath === '/robots.txt') {
    res.writeHead(200, { 'Content-Type': 'text/plain; charset=utf-8' });
    res.end('User-agent: *\nAllow: /\n');
    return;
  }

  // HEAD method support
  if (req.method === 'HEAD') {
    res.writeHead(200, {
      'Content-Type': 'text/html; charset=utf-8',
      'X-Content-Type-Options': 'nosniff'
    });
    res.end();
    return;
  }

  // Serve main page at root "/" and all application routes (SPA fallback to prevent 404)
  const html = getIndexHtml();
  res.writeHead(200, {
    'Content-Type': 'text/html; charset=utf-8',
    'X-Content-Type-Options': 'nosniff'
  });
  res.end(html);
});

server.listen(PORT, HOST, () => {
  console.log(`Rozana server successfully listening on http://${HOST}:${PORT}`);
  console.log(`Serving main page at root '/' and all SPA routes`);
});
