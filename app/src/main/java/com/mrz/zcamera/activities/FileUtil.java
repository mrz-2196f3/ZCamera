package com.mrz.zcamera.activities;
/***********************
* MIT License

* Copyright (c) 2023 MrZ

* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:

* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.

* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
**********************/
import android.content.*;
import android.os.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FileUtil {

	 

	
	public static boolean isExistFile(String path) {
		File file = new File(path);
		return file.exists();
	}

	public static void makeDir(String path) {
		if (!isExistFile(path)) {
			File file = new File(path);
			file.mkdirs();
		}
	}

	
	public static String getExternalStorageDir() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	 
	public static File createNewPictureFile(Context context) {
		SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String fileName = date.format(new Date()) + ".jpg";
		return new File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + fileName);
	}
}
