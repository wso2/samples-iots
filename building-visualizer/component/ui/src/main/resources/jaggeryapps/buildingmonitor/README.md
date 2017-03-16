# WSO2 IoT Server Device Floorplan Tool
This feature for WSO2 IoT Server enables the user to visually mark and view the location of multiple IoT devices
registered using WSO2 IoT Server as well as view the status of individual devices and device groups in the building's floor
plan/s. Additionally the user can keep track of their IoT devices in multiple geographic locations.

## What Device Floorplan Tool Does
### Buildings Map
* Mark and manage each building location which devices are located in.
* Save basic building information on each building.
* launch floorplan manager tool upon saving building details.

### Floorplan Editor
* Upload and manage floorplans of multiple floors.
* Draw and name zones in the floorplan using SVGs.
* Edit zones.
* Add and manage locations of IoT devices on a floorplan.(Incomplete)

## Main Usage of Device Floorplan Tool
This tool gives the user the ability to monitor the status of their registered devices through WSO2 IoT Server in a
floorplan level as well as through a geographic level.

## How to Run
* Clone this repo to your local machine.
* Launch index.html through preferred browser.
* The tool will display a map with your current location.

## How to Use
### Mark and Manage Building on the Map
1. To mark a building click "Add Building" and place the building marker on preferred area of the map.
2. Enter building details on the building information collapsible side panel on the right and click "Save".

### Managing Floorplan of New Building Marker
1. Click on "Edit Floors" of saved building to launch the Floorplan Tool on a new tab (Note that it defaults to 1st floor).
2. Select desired floor from the dropdown.
3. To upload a floorplan either drag and drop a floorplan image from your local machine or enter an image URL (Only JPEG or PNG).
4. Once the floorplan shows up on the preview click "Proceed".
5. To define a zone click either one of the 3 SVG shapes (Rectangle, Circle and Polygon) in the drawing utilities bar located on the left.
    *When drawing polygon shape close the shape in order to complete zone.
6. Once selected preferred shape to define a zone draw over the floorplan to outline the rooms of the floorplan. Repeat with preferred shape to draw multiple zones.
7. Click on the "selector" shaped moving tool on the drawing utilities bar and double click on the desired zone to bring up context menu.
   * To name the zone fill the "title" field and click "save".

### Placing a Marker on the Floorplan.
 1. Click on the "device" shaped button.
 2. Then click on a desired position on the floorplan to draw the SVG marker.
 3. To Move marker grab and move it.

