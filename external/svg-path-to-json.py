import sys
import json

from xml.dom import minidom


config = {
  'js_var' : 'map',
}


def parse(svg_file):
    svg = minidom.parse(svg_file)
    paths = svg.getElementsByTagName('path')
    items = {}

    for node in paths:
        if node.getAttributeNode('id'):
            path_id = str(node.getAttributeNode('id').nodeValue)
            path = str(node.getAttributeNode('d').nodeValue)
            items[path_id] = path

    return 'var {0} = {1}'.format(
        config['js_var'], json.dumps(items, indent=2),
    )


def main():
    if len(sys.argv) < 2:
        print (u'Usage: python svg-to-json.py some_svg.svg')
        return

    sys.stdout.write(parse(sys.argv[1]))


if __name__ == '__main__':
    main()