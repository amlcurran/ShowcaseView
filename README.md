Showcase View library  
====
  
The ShowcaseView library is designed to highlight and showcase specific parts of apps to the user with a distinctive and attractive overlay. This library is great for pointing out points of interest for users, gestures, or obscure but useful items.

The library is based on the "Cling" view found in the Launcher on Ice-Cream Sandwich and Jelly Bean, but extended to be easier to use.

Please check out [the website](http://espiandev.github.com/ShowcaseView) for more information.

![Example image](https://raw.github.com/Espiandev/ShowcaseView/master/example.png)


Setup 

-----

1. Download old nine library to make sure showcase view works for you. https://github.com/JakeWharton/NineOldAndroids
2. Import nine old library to your eclipse workspace [ File -> Import -> Existing Android Code Into Workspace
3. Download Showcase View and import the showcase library the same way.
4. At this point eclipse may give some compile errors to the showcase view library becuase we need to add nine old library as supporting library for showcase view.
5. Right click the showcase view library folder inside eclipse workspace.
6. Go to properties -> Android -> Add -> select nine old library -> Hit Apply and you are done.


Usage
----

v2 brings the ability to showcase items on the ActionBar. Currently built in showcase-able things are:
- The home button 
- Your application title or a Spinner if you're using that navigation type
- Any ActionItem - requires only the item's ID
- The Overflow icon   

Gestures can now be indicated using `animateGesture(...)`, which returns a [NineOldAndroids](http://nineoldandroids.com) `AnimatorSet`, which can be gestured simply by calling `start()` on it. If you want to make your own gestures, `getHand()` will return the View which contains the Ghostly Hand. You can also quickly point to something using the `pointTo(..)` methods.

Styles are included to maintain consistently in ShowcaseViews. Buttons should use the style ClingButton, with title text using ClingTitleText and standard text using ClingText.

Upcoming features
----

- Improved styling ability
- Variable sized circles/images for showcasing

Copyright and Licensing
----

Copyright Alex Curran ([+Alex](https://plus.google.com/110510888639261520925/posts)) © 2012. All rights reserved.

This library is disributed under an Apache 2.0 License.
