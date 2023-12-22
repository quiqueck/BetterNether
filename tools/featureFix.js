const fs = require('fs')
    path = require('path')

const searchBCLConfiguration = /public static final BCLConfigureFeature(?:\s*\<[^>]*\>)?\s*(\w+)\s*=\s*([^;]*)\.build\(\s*\);/gs;
const searchBCLPlacement = /BCLFeature(?:\s*\<[^>]*\>)?\s*(\w+)\s*=\s*([^;]+)\.place\s*\(\s*\)([^;]*)\.build\s*\(\s*\)\s*;/gs;
;
const cfgSource = 'NetherOres'
const plcSource = cfgSource+'Placed'
const configInput = '../src/main/java/org/betterx/betternether/registry/features/configured/'+cfgSource+'.java'
const placementInput = '../src/main/java/org/betterx/betternether/registry/features/placed/'+plcSource+'.java'


let file = fs.readFileSync(path.join(__dirname, configInput), 'utf-8')
let filePlacement = fs.readFileSync(path.join(__dirname, placementInput), 'utf-8')
const onlyInBiome = ['onlyInBiome','vanillaNetherGround', 'betterNetherGround', 'betterNetherCeiling', 'betterNetherOnWall', 'betterNetherInWall']

function getAll(text, regex) {
    const result = [];
    let match;
    while (match = regex.exec(text)) {
        result.push(match);
    }
    return result;
}

function splitParameters(parametersString) {
    const result = [];
    let currentParameter = '';
    let openParentheses = 0;
    let foundFirst = false;

    for (let i = 0; i < parametersString.length; i++) {
        const char = parametersString[i];

        if (char === ',' && openParentheses === 1) {
            result.push(currentParameter.trim());
            currentParameter = '';
        } else {
            if (char === '(') {
                foundFirst = true;
                if (openParentheses>0) currentParameter += char;
                openParentheses++;
            } else if (char === ')') {
                openParentheses--;
                if (openParentheses>0) currentParameter += char;
                else if (openParentheses===0){
                    parametersString = parametersString.substring(i+1);
                    break;
                }
            } else {
                if (openParentheses>0) currentParameter += char;
            }
        }
    }

    // Add the last parameter
    result.push(currentParameter.trim());

    return {trim: parametersString, params:result};
}

const toFix = []
const toFixPlacements = []
const startTranslations = {
    'startColumn': (params, id)=> [`.blockColumn(C.id("${id}"))`, '', '<AsBlockColumn>'],
    'startPillar': (params, id)=> [`.pillar(C.id("${id}"))`, `.transformer(${params[1]})`,'<AsPillar>'],
    'startOre': (params, id)=> [`.ore(C.id("${id}"))`, '','<AsOre>'],
    'startBonemealPatch': (params, id)=> [`.bonemeal(C.id("${id}"))`, '','<WeightedBlockPatch>'],
    'startBonemealNetherVegetation': (params, id)=> [`.bonemealNetherForrest(C.id("${id}"))`, '','<NetherForrestVegetation>'],
    'start': (params, id) =>{
        if (params[1].toLowerCase().indexOf('feature')>=0){
            return [
            `.configuration(C.id("${id}"), ${params[1]})
// .simple(C.id("${id}"))
`,
            `// .addBlock(${params[1]})
`,'']
        }
        return [
        `.simple(C.id("${id}"))
// .configuration(C.id("${id}"), ${params[1]})
`,
        `.addBlock(${params[1]})`,
        '<ForSimpleBlock>'
        ]
    }
}
while ((m = searchBCLConfiguration.exec(file)) !== null) {
    // This is necessary to avoid infinite loops with zero-width matches
    if (m.index === searchBCLConfiguration.lastIndex) {
        searchBCLConfiguration.lastIndex++;
    }

        // The result can be accessed through the `m`-variable.
//        m.forEach((match, groupIndex) => {
//            console.log(`Found match, group ${groupIndex}: ${match}`);
//        });
    const obj = {
                 all: m[0],
                 varName: m[1].trim(),
                 commands: m[2].trim(),
    };
    obj.ids = getAll(obj.commands, /BN\.id\("(\w+)"\)/gs).map((h)=>h[1]);
    obj.cfg = `/* Not Implemented ${obj.varName} */;`;

    const parts = obj.commands.split('.inRandomPatch');
    if (parts.length>1){
        const split = splitParameters(parts[1]);
        if (split.params.length>0){
            obj.commands = obj.commands.replace(split.params[0], '');
        }
    }

    if (obj.commands.startsWith('BCLFeatureBuilder')){
        obj.orgStart = getAll(obj.commands, /BCLFeatureBuilder\s*\.(\w+)/gs)[0][1]
        const split = splitParameters(obj.commands);
        obj.orgStartParams = split.params;
        obj.commands = split.trim;
        if (startTranslations[obj.orgStart] === undefined) {
            console.log("Unknown start: " + obj.orgStart)
             obj.cfg = `/* Unknown start ${obj.orgStart} for ${obj.varName} */;`;
        } else {
            obj.start = startTranslations[obj.orgStart](obj.orgStartParams, obj.ids[obj.ids.length-1])
            obj.commands = obj.start[1]+obj.commands;
            obj.cfg = `public static final ConfiguredFeatureKey${obj.start[2]} ${obj.varName} =
                ConfiguredFeatureManager${obj.start[0]};`
        }
    } else {
            obj.start = [`.randomPatch(C.id("${obj.ids[obj.ids.length-1]}"))`,`.featureToPlace(${plcSource}.${obj.varName})`, '<RandomPatch>']
            obj.cfg = `public static final ConfiguredFeatureKey${obj.start[2]} ${obj.varName} =
                    ConfiguredFeatureManager${obj.start[0]};`
        obj.placedFeature = getAll(obj.commands, /(\w+)\s*\.place/gs)[0][1]

        obj.commands = getAll(obj.commands, /.place\s*\(\s*\)\s*([^;]*)/gm)[0][1]
        const placement = {
            varName: "PLACED_"+obj.placedFeature,
            commands:'',
            type:'PlacedConfiguredFeatureKey',
            init: `PlacedFeatureManager
                               .createKey(${cfgSource}.${obj.placedFeature});`,
        }
        const parts = obj.commands.split('.inRandomPatch(')
        if (parts.length>1){
            obj.commands = '.featureToPlace('+plcSource+'.PLACED_'+obj.placedFeature+parts[1].trim()
            placement.commands = parts[0].trim()
        }
        toFixPlacements.push(placement)
    }

    toFix.push(obj);
}

while ((m = searchBCLPlacement.exec(filePlacement)) !== null) {
    // This is necessary to avoid infinite loops with zero-width matches
    if (m.index === searchBCLPlacement.lastIndex) {
        searchBCLPlacement.lastIndex++;
    }

        // The result can be accessed through the `m`-variable.
//        m.forEach((match, groupIndex) => {
//            console.log(`Found match, group ${groupIndex}: ${match}`);
//        });
    const obj = {
                 all: m[0],
                 varName: m[1].trim(),
                 type:'PlacedConfiguredFeatureKey',
                 feature: m[2].trim().replace(/\s/gs,''),
                 commands: m[3].trim(),
    };
    obj.init = `PlacedFeatureManager.createKey(${obj.feature})`
    const deco = getAll(obj.commands, /\.decoration\(([^)]*)\)/gs);
    if (deco.length>0){
        obj.commands = obj.commands.replace(deco[0][0], '');
        obj.init+=`.setDecoration(${deco[0][1]})`
    }
    obj.init+= ';'

    //console.log(obj.varName, obj.feature)
    toFixPlacements.push(obj);
}



console.log("Configuration Definitions ------------------------------------------------")
const fields = toFix.map((o) => o.cfg).join('\n')
    fs.writeFileSync(path.join(__dirname, "Configuration.java"), fields)
console.log(fields)

console.log("Placement Definitions ------------------------------------------------")
const pFields = toFixPlacements.map((o) => `public static final ${o.type} ${o.varName} = ${o.init}`).join('\n')
    fs.writeFileSync(path.join(__dirname, "Placements.java"), pFields)
console.log(pFields)


console.log("ConfigurationProvider ------------------------------------------------")
const configure = toFix.map((o) => `${cfgSource}.${o.varName}
    .bootstrap(context)
    ${o.commands}
    .register();`).join('\n')
fs.writeFileSync(path.join(__dirname, "ConfigurationDataProvider.java"), configure.replace(/$\s*$/gm,'' ))
console.log(configure)

console.log("Modified Provider ------------------------------------------------")
const placements = toFixPlacements.map((o) => `${plcSource}.${o.varName}
                      .place(context)
                      ${o.commands}
                      .register();`
).join('\n')
fs.writeFileSync(path.join(__dirname, "DataProvider.java"), placements.replace(/$\s*$/gm,'' ))
console.log( placements )