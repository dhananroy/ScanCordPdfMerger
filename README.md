# ScanCordPdfMerger

ScanCordPdfMerger is a library for merge multiple pdf in single pdf file for Android, the size of this library is less than 100KB
## Usage

ScanCordPdfMerger is available at JitPack's Maven repo.

If you're using Gradle, you could add NewPipe Extractor as a dependency with the following steps:

1. Add `maven { url 'https://jitpack.io' }` to the `repositories` in your `build.gradle`.
2. Add `implementation 'com.github.dhananroy:ScanCordPdfMerger:INSERT_VERSION_HERE'` to the `dependencies` in your `build.gradle`. Replace `INSERT_VERSION_HERE` with the [latest release](https://github.com/dhananroy/ScanCordPdfMerger/releases).

```groovy
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
```groovy
dependencies {
	           implementation 'com.github.dhananroy:ScanCordPdfMerger:1.0'
	}
```

```groovy
                    MergePDF("File1PathInString", "File2PathInString", "FileOutPutPathInString", "OutPutFileName.pdf");	    
```
