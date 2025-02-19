# gdx-TinyVG

A libgdx parser and renderer for TinyVG https://www.tinyvg.tech/

[![Build, Test, Publish](https://github.com/lyze237/gdx-TinyVG/workflows/Test/badge.svg?branch=main)](https://github.com/lyze237/gdx-TinyVG/actions?query=workflow%3A%22Test%22)
[![License](https://img.shields.io/github/license/lyze237/gdx-TinyVG)](https://github.com/lyze237/gdx-TinyVG/blob/main/LICENSE)
[![Jitpack](https://jitpack.io/v/lyze237/gdx-TinyVG.svg)](https://jitpack.io/#lyze237/gdx-TinyVG)

# What is TinyVG?

SVG is a horribly complex format and an overkill for most projects. The specification includes way too many edge cases so implementing a new SVG renderer will always have drawbacks or missing pieces.

TinyVG tries to be simpler. Fewer features, but powerful enough to cover 90% of use cases. ~ https://tinyvg.tech

# Convert SVG into TVG

Convert your SVG into a TVG here: https://svgtotvg.herokuapp.com/

Some things are not supported.

# Example

```java
public class Lwjgl3Launcher {
  public static void main(String[] args) {
    createApplication();
  }

  private static Lwjgl3Application createApplication() {
    return new Lwjgl3Application(new Main(), getDefaultConfiguration());
  }

  private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
    Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
    // By default, stencils are disabled, we need those though. So let's enable them here (It's the 6th value. Change that to >= 2).
    configuration.setBackBufferConfig(8, 8, 8, 8, 16, 2, 0);
    // ...

    // In a legacy desktop project it's:
    config.stencils = 2; // >= 2
    return configuration;
  }
}

public class Example extends ApplicationAdapter {
  private TinyVG tvg;
  private TinyVGShapeDrawer drawer;
  private Viewport viewport = new XXXViewport(xxx, xxx);

  public void create() {
    var assetLoader = new TinyVGAssetLoader();
    tvg = assetLoader.load("file.tvg"); // only works with the binary file format

    // TinyVGShapeDrawer requires a 1x1 white pixel file,
    // so create one in your favourite program or
    // download one from the test examples in the repo.
    // https://github.com/lyze237/gdx-TinyVG/blob/main/src/test/resources/pixel.png
    drawer = new TinyVGShapeDrawer(new SpriteBatch(), new TextureRegion(new Texture("pixel.png")));

    // You're also able to create a texture region directly out of a tvg file:
    var tvgRegion = TinyVGIO.toTextureRegion(tvg, drawer);
  }

  public void render() {
    Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT); // GL20.GL_STENCIL_BUFFER_BIT is very important

    viewport.apply();

    drawer.getBatch().setProjectionMatrix(viewport.getCamera().combined);

    drawer.getBatch().begin();
    tvg.draw(drawer, viewport);
    drawer.getBatch().end();
  }

  @Override
  public void resize(int width, int height) {
    viewport.update(width, height, true);
  }
}
```

Alternatively you can load it via an AssetManager
```java
public class Example extends ApplicationAdapter {
  private TinyVG tvg;
  private TinyVGShapeDrawer drawer;
  private Viewport viewport = new XXXViewport(xxx, xxx);

  public void create() {
    var assetManager = new AssetManager();
    assetManager.setLoader(TinyVG.class, new TinyVGAssetLoader());

    assetManager.load("square.tvg", TinyVG.class);

    assetManager.finishLoading();

    var tvg = assetManager.get("square.tvg", TinyVG.class);
  }
}
```
Or directly create a texture region out of it:
```java
public class Example extends ApplicationAdapter {
  public void create() {
    var drawer = new TinyVGShapeDrawer(new SpriteBatch(), new TextureRegion(new Texture("pixel.png")));

    var assMan = new AssetManager();
    assMan.setLoader(TinyVG.class, new TinyVGAssetLoader());
    assMan.setLoader(TinyVGTextureAssetLoader.Result.class, new TinyVGTextureAssetLoader());

    assMan.load(file, TinyVGTextureAssetLoader.Result.class, new TinyVGTextureAssetLoader.Parameters(drawer));

    assMan.finishLoading();

    tvg = assMan.get(file, TinyVGTextureAssetLoader.Result.class).getTextureRegion();
  }
}
```

## Setters

The TinyVG class comes with a couple setters to adjust render options:

* `setPosition()` => Changes the origin (bottom left corner) of the sprite.
* `setScale()` => Changes the scale.
* `setLineWidthScale()` => Changes the scale for all line widths independent of `setScale()`.
* `.setCurvePoints()` => The amount of points per path curve (Bezier, Arc) is used to calculate the curve.

# Specification implementation status

https://tinyvg.tech/download/specification.pdf

Everything is implemented according to the specification.pdf except:
* [Partially] Draw Line Path (No variable line width, help wanted)
* [Partially] Outline Fill Path (No variable line width, help wanted)

# Installation

0. Add [ShapeDrawer](https://github.com/earlygrey/shapedrawer) as dependency (See their readme for instructions).

1. Open or create `gradle.properties` in the root folder of your project, add the following line:

```properties
gdxTinyVGVersion=VERSION
```

Check [Jitpack](https://jitpack.io/#lyze237/gdx-TinyVG/) for the latest version and replace `VERSION` with that.

2. Add the jitpack repo to your build file.

```groovy
allprojects {
    repositories {
        // ...
        maven { url 'https://jitpack.io' }
    }
}
```

3. Add that to your core modules dependencies inside your root `build.gradle`

```groovy
project(":core") {
    // ...

    dependencies {
        // ...
        implementation "com.github.lyze237:gdx-TinyVG:$gdxTinyVGVersion"
    }
}
```

## Html/Gwt project

1. Gradle dependency:

```groovy
implementation "com.github.lyze237:gdx-TinyVG:$gdxTinyVGVersion:sources"
```

2. In your application's `.gwt.xml` file add (Normally `GdxDefinition.gwt.xml`):

```xml

<inherits name="dev.lyze.tinyvg"/>
```

## Help

* Fill paths are displayed garbled
  * Make sure to set `| GL20.GL_STENCIL_BUFFER_BIT` in your clear method. See [example](#example).
  * Make sure to have `config.stencil` enabled and set to >= 2 in your launcher configuration.

## How to test

By default, if you run `./gradlew test` gradle runs headless tests. If you want to test `lwjgl` tests (so with an actual
gui), then you need to run them with `./gradlew test -Plwjgl=true`

Set environment variable `SLEEPY` to a millisecond number to sleep between each LWJGL test. (For example: SLEEPY=3000 would wait 3 seconds after every test.)
