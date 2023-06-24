const fs = require('fs/promises')
const nbt = require('prismarine-nbt')
const zlib = require('zlib')

async function processFile(file) {
    console.log('\n----- Processing file', file)
    const buffer = await fs.readFile(file)
    const { parsed, type } = await nbt.parse(buffer)
     
    const palette = parsed.value.palette.value.value
    const blocks = parsed.value.blocks.value.value
    //filter all elements from palette where the Name attribute contains either chest or barrel
    const storageBlocks = palette
        .map((element, idx) => {return { name:element.Name.value, idx:idx};}) 
        .filter(element => element.name.indexOf('chest')>=0 || element.name.indexOf('barrel')>=0)
    
    //console.log(storageBlocks)
    let changes = false;
    let pos =0;
    for (element of blocks){
        const storage = storageBlocks.find(e => e.idx == element.state.value);
        const nbt = element.nbt;
        if (storage!==undefined){
            let table = "NONE"
            if (nbt!==undefined &&nbt.value!==undefined && nbt.value.LootTable!==undefined)
                table = nbt.value.LootTable.value

            if (table === "NONE"){
                changes = true
                //randomly pick one of two strings weighted
                table = Math.random() < 0.3 ? 'betternether:chests/city_surprise' : 'betternether:chests/city_common'                
                nbt.value.LootTable = { type: 'string', value: table }                                    
                delete nbt.value.Items
                table = "[NEW] " + table
            }
            console.log(storage.name, '->', table, `(${pos})`)
        }
        pos++;
    }
        
    if (changes){
        console.log("WRITING")
        const nbtData = nbt.writeUncompressed(parsed, type)            

        const nbtDataCompressed = await new Promise((resolve, reject) => {
            zlib.gzip(nbtData, (error, compressed) => {
                if (error) reject(error)
                else resolve(compressed)
            })
        })
        let fl
        try{
            fl = await fs.open(file, 'w');
            fl.createWriteStream().write(nbtDataCompressed)
        } finally {
            await fl?.close();
        }

    }
  }
//compress a buffer with gzip

  

async function main(dir) {
    const files = await fs.readdir(dir)
    for (const file of files) {
        const path = require('path')
        const filePath = path.join(dir, file)
        const stat = await fs.stat(filePath)
        if (stat.isDirectory()) {
            await main(filePath)
        } else {
            await processFile(filePath)
        }
    }
}

main('../src/main/resources/data/betternether/structures/city/')
  