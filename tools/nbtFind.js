const fs = require('fs/promises')
const nbt = require('prismarine-nbt')

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
   blocks
        .map(element => {return { nbt: element.nbt, src:element, storage:storageBlocks.find(e => e.idx == element.state.value)};})
        .filter(element => element.storage!==undefined)
        .forEach(element => {
            let table = "NONE"
            if (element.nbt!==undefined && element.nbt.value!==undefined && element.nbt.value.LootTable!==undefined)
                table = element.nbt.value.LootTable.value

            if (table === "NONE"){
                changes = true
                //randomly pick one of two strings weighted
                table = Math.random() < 0.3 ? 'betternether:chests/city_surprise' : 'betternether:chests/city_common'                
                element.src.nbt.value.LootTable = { type: 'string', value: table }                                    
                delete element.src.nbt.value.Items
                table = "[NEW] " + table
            }
            console.log(element.storage.name, '->', table)
        });   
        
        if (changes){
            console.log("WRITING")
            const nbtData = nbt.writeUncompressed(parsed, type)

            let fl
            try{
                fl = await fs.open(file, 'w');
                fl.createWriteStream().write(nbtData)
             } finally {
                await fl?.close();
             }
            
        }
  }

  
// recursivley gets all files in the directory passed as a parameter and calls processFile for each file
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
  