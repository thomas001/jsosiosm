# jsosiosm

## Motivation
I was simply not satisfied with the quality of the OSM coverage of norway. More concrete paths i wanted to take for my hiking tour were not in the map. Hence i searched for different map sources and found the official maps available from the Norwegian mapping authority. Unfortunately, they use there own format, SOSI, which needs to be converted to OSM.

## State
The program understands most of the objects in the "N50" maps and translates them to usable OSM tags. SOSI polygons are correctly translated into multi polygons. The main part missing is the inclusion of the name database, only mountain and town names are currently used.

## Technology
The program is written in Java (I had a toy python implementation before, but it was too slow). Java seems to be quite popular in the OSM community (JOSM, mkgmap,...). Input files can be .sos or .sos.gz. I do not use the official SOSI parsing library [FYBA](https://github.com/kartverket/fyba), but my own parser implementation. Output files can be .osm or .osm.pbf. All translations are currently implemented directly in Java, though stub support for loading javascript to do the translation is there.

## Example
```
java -jar JSOSI.OSM -o map.osm.pbf *.sos.gz
```
will create map.osm.pbf from all gzipped sosi files. 

### Bugs
* JOSM does not read the .osm.pbf files, as it complains about negative IDs (it's okay with them in .osm files)
* OSMOSIS does not like the .osm files, because of missing metadata (timestamp,...). OSMOSIS reads .osm.pbf file
