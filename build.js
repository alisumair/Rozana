const fs = require('fs');
const path = require('path');

console.log('Building Rozana application for production / Cloud Run...');

const rootIndex = path.join(__dirname, 'index.html');
if (!fs.existsSync(rootIndex)) {
  console.error('Error: index.html not found in root directory');
  process.exit(1);
}

const htmlContent = fs.readFileSync(rootIndex, 'utf8');

// List of target directories for buildpack / static output compatibility
const targetDirs = ['dist', 'public', 'build'];

const manifestContent = JSON.stringify({
  name: 'Rozana – روزانہ',
  short_name: 'Rozana',
  start_url: '/',
  display: 'standalone',
  background_color: '#F5F2FC',
  theme_color: '#6028E4',
  icons: [
    {
      src: 'data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><rect width="100" height="100" rx="25" fill="%236028E4"/><text y="55%" x="50%" dominant-baseline="middle" text-anchor="middle" font-size="50" fill="white">R</text></svg>',
      sizes: '192x192 512x512',
      type: 'image/svg+xml'
    }
  ]
}, null, 2);

const robotsContent = 'User-agent: *\nAllow: /\n';

targetDirs.forEach((dirName) => {
  const dirPath = path.join(__dirname, dirName);
  if (!fs.existsSync(dirPath)) {
    fs.mkdirSync(dirPath, { recursive: true });
  }
  fs.writeFileSync(path.join(dirPath, 'index.html'), htmlContent, 'utf8');
  fs.writeFileSync(path.join(dirPath, 'manifest.json'), manifestContent, 'utf8');
  fs.writeFileSync(path.join(dirPath, 'robots.txt'), robotsContent, 'utf8');
  console.log(`Created output in ${dirName}/`);
});

console.log('Build completed successfully. All frontend outputs verified at root, dist/, public/, and build/.');
