aShot
=====

WebDriver Screenshot utility

* Takes a screenshot of web element from the different device types
* Allows to prettify screenshot images
* Provides the configurable screenshot comparison


#####Include aShot in your project
```
<dependency>
    <groupId>ru.yandex.qatools.ashot</groupId>
    <artifactId>ashot</artifactId>
    <version>1.1</version>
</dependency>
```


#####WebElement view

The objective of taking web element looks simply and consists of three goals:
* Take screenshot of the page
* Find element's size and position   
* Crop original screenshot image

As a result aShot provides the image with a WebElement
![images snippet](/doc/img/images_intent_blur.png)

#####Taking screenshot of page

Different webDrivers have a different behaviour with screenshot taking. Some of them provide a screenshot of whole page or viewport only. AShot can be configured according to exact behaviour. This example configuration allows to take screenshot of whole page when driver provides viewport image only, for example, Chrome, Mobile Safari and etc. 
```java
new AShot()
  .shootingStrategy(new ViewportPastingStrategy())
  .takeScreenshot(webDriver);
```

#####Taking screenshot of web element

To take a screenshot of element we just need to set the locator.
 ```java
 new AShot()
   .componentSelector(By.cssSelector("#my_element"))
   .takeScreenshot(webDriver);
 ```
 
 In this case aShot will find element's location and position and will crop origin image. WebDriver API gives an opportunity to find coordinates of web element, but also different WebDriver implementations provides differently. In my opinion the most universal way to find coordinates is to use jQuery for it. AShot uses jQuery by default. But some drivers have a problems with Javascript execution such as Opera. In this case it's better to use another way to find web element coordinates.
  ```java
   new AShot()
     .componentSelector(By.cssSelector("#my_element"))
     .coordsProvider(new WebDriverCoordsProvider()) //find coordinated using WebDriver API
     .takeScreenshot(webDriver);
   ```
 Of course, if you don't like to use jQuery, you can implement you own CoordsProvider and contribute this project.
 
#####Prettifying web element screenshot

So, let's take a simple screenshot of weather snippet at Yandex.com.

 ```java
 new AShot()
   .componentSelector(By.cssSelector("#weather"))
   .takeScreenshot(webDriver);
 ```
 AShot cropped origin images and we can see this result.
 ![simple weather snippet](/doc/img/def_crop.png)
 
 In default case DefaultCropper is using. But there is a way to use another cropper
 
 ```java
  new AShot()
    .componentSelector(By.cssSelector("#weather"))
    .withCropper(new IndentCropper() //overwriting cropper
                    .addIndentFilter(blur())) //adding filter for indent
    .takeScreenshot(driver);
  ```
  
  ![indent blur weather snippet](/doc/img/weather_indent_blur.png)
  This screenshot provides more information about element's position relatively his siblings and blurs indent to focus view on web element.
  
#####Comparison of screenshots
As you noticed, the ```.takeScreenshot()``` returns a Screenshot object, containing screenshot image and data for comparison.

```java
  Screenshot myScreenshot = new AShot()
    .componentSelector(By.cssSelector(".#weather"))
    .addIgnoredElement(By.cssSelector(".#weather .blinking_element")) //ignored element
    .takeScreenshot(driver);
```

To get a diff between two images use ImageDiffer:

```java
  ImageDiff diff = new ImageDiffer().makeDiff(myScreenshot, anotherScreenshot);
  BufferedImage diffImage = diff.getMarkedImage(); //megred image with marked diff areas
```


 
 











  
  










    
    
 
  
  
  

 
 
 
 
 
 
 
 
 
 
 
 
 









 
 

 
  
 
   
 
 









