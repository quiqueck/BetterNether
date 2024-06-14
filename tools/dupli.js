const fs = require('fs');
const path = require('path');
const diff = require('deep-diff');

// Function to get all files in a directory
function getAllFiles(dirPath, arrayOfFiles) {
  files = fs.readdirSync(dirPath);

  arrayOfFiles = arrayOfFiles || [];

  files.forEach(function (file) {
    if (fs.statSync(dirPath + "/" + file).isDirectory()) {
      arrayOfFiles = getAllFiles(dirPath + "/" + file, arrayOfFiles);
    } else {
      arrayOfFiles.push(path.join(dirPath, "/", file));
    }
  });

  return arrayOfFiles;
}

// Function to get files common to two directories
function getCommonFiles(dir1, dir2) {
  const filesDir1 = getAllFiles(dir1).map(file => path.relative(dir1, file));
  const filesDir2 = getAllFiles(dir2).map(file => path.relative(dir2, file));

  const commonFiles = filesDir1.filter(file => filesDir2.includes(file));

  return commonFiles;
}

// Function to preprocess file content by removing newlines and spaces
function preprocessContent(content) {
  let obj = JSON.parse(content.replace(/minecraft:/g, ''));
if (obj&&obj.variants&&Array.isArray(obj.variants[""]) && obj.variants[""].length == 1){
    obj.variants[""] = obj.variants[""][0]
}
  return obj;
}

// Function to get the diff of two files
function getFileDiff(file1, file2) {
  const file1Content = preprocessContent(fs.readFileSync(file1, 'utf8'));
  const file2Content = preprocessContent(fs.readFileSync(file2, 'utf8'));

  const differences = diff.diff(file1Content, file2Content);
  return differences;
}

function checkPath(base){
    const directoryPath1 = base.replace('{IN}','resources')
    const directoryPath2 = base.replace('{IN}','generated')

    const commonFiles = getCommonFiles(directoryPath1, directoryPath2);

    if (commonFiles.length) {
      const same = []
      const other = []
      console.log('Common files in both directories:');
      commonFiles.forEach(file => {
            const file1Path = path.join(directoryPath1, file);
            const file2Path = path.join(directoryPath2, file);
            const differences = getFileDiff(file1Path, file2Path);

            if (differences) {
            other.push(file1Path)
            console.log(`${file1Path}:`)
              differences.forEach(diff => {
                console.log("    ", diff);
              });
              console.log('')
            } else {
              same.push(file1Path)
            }
      });
      if (other.length>0){
        console.log('\n\nFiles are different:')
        other.forEach(file => {
          console.log(`    ${file}`)
        });
      }
      if (same.length>0){
        console.log('\n\nFiles are the same:')
        same.forEach(file => {
          console.log(`    ${file}`)
          fs.unlink(file, (err) => {
            if (err) {
              console.error(err)
              return
            }
          })
        });
      }
    } else {
      console.log('No common files found in both directories.');
      return true;
    }

    return false;
}

checkPath('../src/main/{IN}/assets/betternether/blockstates')
checkPath('../src/main/{IN}/assets/betternether/models/block')
checkPath('../src/main/{IN}/assets/betternether/models/item')
