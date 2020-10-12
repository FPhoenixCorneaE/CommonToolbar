# CommonTitlebar
通用标题栏，支持左中右常规标题栏设置、沉浸式状态栏、自定义视图；


How to include it in your project:
--------------
**Step 1.** Add the JitPack repository to your build file
```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

**Step 2.** Add the dependency
```groovy
dependencies {
	implementation 'com.github.FPhoenixCorneaE:CommonTitlebar:1.0.1'
}
```