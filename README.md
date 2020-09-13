# Baking_Udacity 

## Udacity - Android Developer Nanodegree - Project: Make A Baking App

Create an app to view video recipes. Handle media loading, verify the user interfaces with UI tests, and integrate third party libraries. Also provide a complete user experience with a home screen widget.

<p float="left"> 
<img raw="true"  width="620"  alt="screenshot2" src="https://github.com/aencg/.github/blob/master/baking_images/Screenshot_1596100640.png?raw=true">
<img raw="true" width="620" alt="screenshot" src="https://github.com/aencg/.github/blob/master/baking_images/Screenshot_1596100632.png?raw=true">  
 </p>
 

# Project Specifications
## General App Usage
- App should display recipes from provided network resource.
 
- App should allow navigation between individual recipes and recipe steps.
 
- App uses RecyclerView and can handle recipe steps that include videos or images.

 ## Components and Libraries
 
 - Application uses Master Detail Flow to display recipe steps and navigation between them. 

 - Application uses Exoplayer to display videos. 
 
 - Application properly initializes and releases video assets when appropriate. 
 
 - Application should properly retrieve media assets from the provided network links. It should properly handle network requests.
 
 - Application makes use of Espresso to test aspects of the UI.
 
 - Application sensibly utilizes a third-party library to enhance the app's features. That could be helper library to interface with ContentProviders if you choose to store the recipes, a UI binding library to avoid writing findViewById a bunch of times, or something similar. 
 
## Homescreen Widget

 - Application has a companion homescreen widget. 

 - Widget displays ingredient list for desired recipe.
