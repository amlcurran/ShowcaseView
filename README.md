Showcase View library  
====
  
The ShowcaseView library is designed to highlight and showcase specific parts of apps to the user with a distinctive and attractive overlay. This library is great for pointing out points of interest for users, or obscure but useful items.

The library is based on the "Cling" view found in the Launcher on Ice-Cream Sandwich and Jelly Bean, but extended to be easier to use.

![Example image](https://raw.github.com/Espiandev/ShowcaseView/master/example.png)

Usage
----
  
To achieve a full-screen overlay, place a ShowcaseView as high in your layout's view hierarchy as you can. To set the view to showcase, call `ShowcaseView.setShowcaseView(..)`. By default, the ShowcaseView will show every time the layout is shown, but this can be altered by calling `ShowcaseView.setShotType(..)` before `setShowcaseView(..)`. Using `TYPE_ONE_SHOT` will only show the ShowcaseView the first time the layout is shown.

As ShowcaseView extends a RelativeLayout, you can add items to the ShowcaseView, such as a button to hide it or text to explain what you are showcasing. If you give the button the id "@id/showcase_button", it will, by default, close the ShowcaseView once pressed. This behaviour can be overridden by using `ShowcaseView.overrideButtonClick(..)`.

Styles are included to maintain consistently in ShowcaseViews. Buttons should use the style ClingButton, with title text using ClingTitleText and standard text using ClingText.