const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = process.env.PORT || process.env.DEFAULT_APP_PORT || 3000;
const indexPath = path.join(__dirname, 'index.html');

const server = http.createServer((req, res) => {
  fs.readFile(indexPath, 'utf8', (err, data) => {
    if (err) {
      res.writeHead(500, { 'Content-Type': 'text/plain' });
      res.end('Error loading app');
      return;
    }
    res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
    res.end(data);
  });
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`Rozana app running on http://0.0.0.0:${PORT}`);
});
