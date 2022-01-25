import sys
import xml.etree.ElementTree as ET

def main(args):
    f = open("vessel_sitemap_0_ships.txt", "w")
    tree = ET.parse('vessel_sitemap_0.xml')
    root = tree.getroot()
    for child in root:
        for each in child:
            if (each.tag.find('loc') != -1):
                # Write to file
                f.write(each.text + '\n') 
                
    f.close()

if __name__ == '__main__':
    main(sys.argv)