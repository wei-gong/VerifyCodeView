VerifyCodeView
==========
#### VerifyCodeView is a android view for inputting Verification Code.

Features
===========

- #### Numerical verification code.

- #### Customize appearance of border. 

- #### Automatic fill verification code by SMS.

ScreenCapture
===========

![CircleView](/images/verifycode_display.gif)

![](/images/auto_filled.gif)

Dependency
===========

- #### Gradle

```groovy
dependencies {
    compile 'com.github.gongw:verifycodeview:1.0.2'
}
```

- #### Maven

```xml
<dependency>
  <groupId>com.github.gongw</groupId>
  <artifactId>verifycodeview</artifactId>
  <version>1.0.2</version>
  <type>pom</type>
</dependency>
```




Attributes
=========

* `vcTextCount` - The verification code text count.
* `vcTextColor` - The verification code text color.
* `vcTextSize` - The verification code text size by sp.
* `vcTextFont` - The verification code text font path in assets.
* `vcDividerWidth` - The divider width by dp between verify code item.
* `vcWrapper` - The wrapper contains verify code.
* `vcWrapperStrokeWidth` - The stroke width of wrapper by dp.
* `vcWrapperColor` - The color of wrapper.
* `vcNextWrapperColor` - The color of wrapper which is the next one to be filled.



Example
=======

- #### xml

```xml
<com.github.gongw.VerifyCodeView
	android:layout_width="240dp"
	android:layout_height="50dp"
	android:layout_marginTop="42dp"
	app:vcTextColor="#b63b21"
	app:vcTextCount="4"
	app:vcTextSize="36sp"
	app:vcDividerWidth="8dp"
	app:vcWrapper="centerLine"
	app:vcWrapperColor="#313335"
	app:vcNextWrapperColor="#b63b21"
	app:vcWrapperStrokeWidth="2dp" />
```

 

- #### java

```java
verifycodeView.setOnAllFilledListener(new VerifyCodeView.OnAllFilledListener() {
        @Override
        public void onAllFilled(String text) {
            Toast.makeText(MainActivity.this, "filled by "+text, Toast.LENGTH_SHORT).show();
        }
    });
```



- #### Customize Wrapper


```java
verifycodeView.setVcWrapper(new VerifyCodeWrapper() {
            @Override
            public boolean isCovered() {
                //whether the wrapper and verify code display together
                return false;
            }

            @Override
            public void drawWrapper(Canvas canvas, Paint paint, RectF rectF, RectF textRectF) {
				//draw your own wrapper
        		canvas.drawLine(textRectF.left - textRectF.width()/2, rectF.height()/2, 									textRectF.right + textRectF.width() / 2, rectF.height()/2, 									paint);
            }
        });
```




- #### Auto filled by SMS

```java
SmsVerifyCodeFilter filter = new SmsVerifyCodeFilter();
filter.setSmsSenderStart("1096");
filter.setSmsSenderContains("5225");
filter.setSmsBodyStart("验证短信：");
filter.setSmsBodyContains("验证码");
filter.setVerifyCodeCount(verifyCodeView.getVcTextCount());
verifyCodeView.startListen(filter);
```


