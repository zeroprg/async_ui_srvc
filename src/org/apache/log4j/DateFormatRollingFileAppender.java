/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j;

import java.io.IOException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.Writer;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.helpers.CountingQuietWriter;
/**
   DateFormatRollingFileAppender extends {@link FileAppender} so each log is 
   named based on a date format defined in the File property.

   Samples File: 'logs/'yyyy/MM-MMM/dd-EEE/HH-mm-ss-S'.log'
   Makes a file like: logs/2004/04-Apr/13-Tue/09-45-15-937.log
   @author James Stauffer */
public class DateFormatRollingFileAppender extends FileAppender {

	/**
    The default maximum file size is 10MB.
	 */
	protected long maxFileSize = 10*1024*1024;

	/**
    There is one backup file by default.
	 */
	protected int  maxBackupIndex  = 1;
	private long maxLogTime=60*60*1000;
	private long nextTime = 0l;
	private SimpleDateFormat sdf =null;
	/**
     The default constructor does nothing. */
	public DateFormatRollingFileAppender() {
		super();
	}

	/**
    Instantiate a <code>DailyRollingFileAppender</code> and open the
    file designated by <code>filename</code>. The opened filename will
    become the ouput destination for this appender.
	 */
	public DateFormatRollingFileAppender (Layout layout, String filename) throws IOException {
		super(layout, filename, true);
	}

	public synchronized void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize)
	throws IOException {
		sdf = new SimpleDateFormat(fileName);
		String actualFileName = sdf.format(new Date());
		makeDirs(actualFileName);
		this.setRollingFile(actualFileName, append, bufferedIO, bufferSize);
	}

	/**
	 * Ensures that all of the directories for the given path exist.
	 * Anything after the last / or \ is assumed to be a filename.
	 * @author James Stauffer
	 */
	private void makeDirs (String path) {
		int indexSlash = path.lastIndexOf("/");
		int indexBackSlash = path.lastIndexOf("\\");
		int index = Math.max(indexSlash, indexBackSlash);
		if(index > 0) {
			String dirs = path.substring(0, index);
			new File(dirs).mkdirs();
		}
	}
	/**
  Returns the value of the <b>MaxBackupIndex</b> option.
	 */
	public int getMaxBackupIndex() {
		return maxBackupIndex;
	}
	/**
  Get the maximum size that the output file is allowed to reach
  before being rolled over to backup files.

  @since 1.1
	 */
	public long getMaximumFileSize() {
		return maxFileSize;
	}
	/**
  Set the maximum number of backup files to keep around.

  <p>The <b>MaxBackupIndex</b> option determines how many backup
  files are kept before the oldest is erased. This option takes
  a positive integer value. If set to zero, then there will be no
  backup files and the log file will be truncated when it reaches
  <code>MaxFileSize</code>.
	 */
	public void setMaxBackupIndex(int maxBackups) {
		this.maxBackupIndex = maxBackups;
	}

	/**
  Set the maximum size that the output file is allowed to reach
 before being rolled over to backup files.

  <p>This method is equivalent to {@link #setMaxFileSize} except
  that it is required for differentiating the setter taking a
  <code>long</code> argument from the setter taking a
  <code>String</code> argument by the JavaBeans {@link
  java.beans.Introspector Introspector}.

  @see #setMaxFileSize(String)
	 */
	public void setMaximumFileSize(long maxFileSize) {
		this.maxFileSize = maxFileSize;
//		this.maxLogTime=maxFileSize;
//		this.nextTime = System.currentTimeMillis()+maxLogTime;
	}


	/**
  Set the maximum size that the output file is allowed to reach
 before being rolled over to backup files.

  <p>In configuration files, the <b>MaxFileSize</b> option takes an
  long integer in the range 0 - 2^63. You can specify the value
  with the suffixes "KB", "MB" or "GB" so that the integer is
  interpreted being expressed respectively in kilobytes, megabytes
  or gigabytes. For example, the value "10KB" will be interpreted
  as 10240.
	 */
	public void setMaxFileSize(String value) {
		maxFileSize = OptionConverter.toFileSize(value, maxFileSize + 1);
	}
	protected void setQWForFiles(Writer writer) {
		this.qw = new CountingQuietWriter(writer, errorHandler);
	}

	/**
      This method differentiates RollingFileAppender from its super
      class.

      @since 0.9.0
	 */
	protected void subAppend(LoggingEvent event) {
		if(maxBackupIndex == 0) {
			long nowTime = System.currentTimeMillis();
			if (nextTime==0){
				nextTime=nowTime+maxLogTime;
			}
			if (nowTime >= nextTime) {
				nextTime=nowTime+maxLogTime;
				this.rollOver();
			}
		}
		
		super.subAppend(event);
		
		
		if(maxBackupIndex >0) {
			if((fileName != null) &&
					((CountingQuietWriter) qw).getCount() >= maxFileSize)
				this.rollOver();
		}
	}
	/**
   Implements the usual roll over behaviour.

   <p>If <code>MaxBackupIndex</code> is positive, then files
  {<code>File.1</code>, ..., <code>File.MaxBackupIndex -1</code>}
   are renamed to {<code>File.2</code>, ...,
   <code>File.MaxBackupIndex</code>}. Moreover, <code>File</code> is
  renamed <code>File.1</code> and closed. A new <code>File</code> is
  created to receive further log output.

  <p>If <code>MaxBackupIndex</code> is equal to zero, then the
  <code>File</code> is truncated with no backup files created.

	 */
	public void rollOver() {
		File target;
		File file;

		LogLog.debug("rolling over count=" + ((CountingQuietWriter) qw).getCount());
		LogLog.debug("maxBackupIndex="+maxBackupIndex);

		// If maxBackups <= 0, then there is no file renaming to be done.
		if(maxBackupIndex > 0) {
			// Delete the oldest file, to keep Windows happy.
			file = new File(fileName + '.' + maxBackupIndex);
			if (file.exists())
				file.delete();

			// Map {(maxBackupIndex - 1), ..., 2, 1} to {maxBackupIndex, ..., 3, 2}
			for (int i = maxBackupIndex - 1; i >= 1; i--) {
				file = new File(fileName + "." + i);
				if (file.exists()) {
					target = new File(fileName + '.' + (i + 1));
					LogLog.debug("Renaming file " + file + " to " + target);
					file.renameTo(target);
				}
			}

			// Rename fileName to fileName.1
			target = new File(fileName + "." + 1);

			this.closeFile(); // keep windows happy.

			file = new File(fileName);
			LogLog.debug("Renaming file " + file + " to " + target);
			file.renameTo(target);
			try {
				// This will also close the file. This is OK since multiple
				// close operations are safe.
				this.setRollingFile(fileName, false, bufferedIO, bufferSize);
			}
			catch(IOException e) {
				LogLog.error("setFile("+fileName+", false) call failed.", e);
			}
		}else if(maxBackupIndex == 0){
			// Delete the oldest file, to keep Windows happy.
			file = new File(fileName + ".old");
			if (file.exists())
				file.delete();
			// Rename fileName to fileName.1
			target = new File(fileName + ".old");
			
			this.closeFile(); // keep windows happy.
			
			file = new File(fileName);
			LogLog.debug("Renaming file " + file + " to " + target);
			file.renameTo(target);
			try {
				// This will also close the file. This is OK since multiple
				// close operations are safe.
				this.setRollingFile(sdf.format(new Date()), false, bufferedIO, bufferSize);
			}
			catch(IOException e) {
				LogLog.error("setFile("+fileName+", false) call failed.", e);
			}
		}

	}
	public synchronized void setRollingFile(String fileName, boolean append, boolean bufferedIO, int bufferSize)
	throws IOException {
		super.setFile(fileName, append, this.bufferedIO, this.bufferSize);
		if(append) {
			File f = new File(fileName);
			((CountingQuietWriter) qw).setCount(f.length());
		}
	}

}

