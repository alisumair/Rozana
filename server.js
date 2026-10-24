const http = require('http');
const fs = require('fs');
const path = require('path');

// Determine port: In this container, nginx listens on 8080 and proxies to 3000
const PORT = parseInt(process.env.DEFAULT_APP_PORT || (process.env.PORT === '8080' ? '3000' : (process.env.PORT || '3000')), 10);
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

  // Direct APK Download support (Rozana_v1.1_Updated.apk, Rozana.apk)
  if (urlPath === '/rozana.apk' || urlPath === '/download-apk' || urlPath === '/Rozana.apk' || 
      urlPath === '/Rozana_v1.1_Updated.apk' || urlPath === '/rozana_v1.1.apk' || urlPath === '/rozana-v1.1.apk') {
    const candidatePaths = [
      path.join(__dirname, 'app', 'build', 'outputs', 'apk', 'debug', 'app-debug.apk'),
      path.join(__dirname, 'Rozana_v1.1_Updated.apk'),
      path.join(__dirname, 'public', 'Rozana_v1.1_Updated.apk'),
      path.join(__dirname, 'rozana.apk')
    ];
    let apkPath = null;
    for (const p of candidatePaths) {
      if (fs.existsSync(p)) {
        apkPath = p;
        break;
      }
    }
    if (apkPath) {
      const stat = fs.statSync(apkPath);
      res.writeHead(200, {
        'Content-Type': 'application/vnd.android.package-archive',
        'Content-Length': stat.size,
        'Content-Disposition': 'attachment; filename="Rozana_v1.1_Updated.apk"',
        'Cache-Control': 'no-store, no-cache, must-revalidate, max-age=0',
        'Pragma': 'no-cache',
        'Expires': '0'
      });
      const readStream = fs.createReadStream(apkPath);
      readStream.pipe(res);
      return;
    }
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
