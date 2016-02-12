package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class CopyWindow extends Pane {
	public CopyWindow(Stage stage, String origen, String destino) {
		Image imageProcess = new Image(getClass().getResourceAsStream("/resources/process.png"));
        Label process = new Label("", new ImageView(imageProcess));
        process.setTranslateX(150);
        process.setTranslateY(160);

		Image imageSVLogo = new Image(getClass().getResourceAsStream("/resources/svlogo.png"));
        Label SVLogo = new Label("", new ImageView(imageSVLogo));
        SVLogo.setTranslateX(1);
        SVLogo.setTranslateY(1);
        
		Text text = new Text("DECODIFICANDO GPS...");
		text.setFont(Font.font("Helvetica", 20));
		text.setWrappingWidth(300);
		text.setFill(Color.GRAY);
		text.setTextAlignment(TextAlignment.CENTER);
		text.setTranslateX(50);
		text.setTranslateY(120);
		
		Text textDesc = new Text();
		textDesc.setFont(Font.font("Helvetica", 16));
		textDesc.setWrappingWidth(300);
		textDesc.setFill(Color.GRAY);
		textDesc.setTextAlignment(TextAlignment.CENTER);
		textDesc.setTranslateX(50);
		textDesc.setTranslateY(140);
        
		AnimationTimer timeline = new AnimationTimer() {
			int j = 0;
			@Override
			public void handle(long now) {
				j += 5;
				process.setRotate(j);
			}
		};
		timeline.start();

        this.getChildren().addAll(process, text, textDesc, SVLogo);
        
        // THREAD DESDE ACA
		Thread tgps = new Thread(new Runnable() { @Override
		public void run() {
			File folder = new File(origen + "GMetrix/");
			File[] listOfFiles = folder.listFiles();
	        List<File> gpxList = new ArrayList<File>();

	        int countbabel = 0;
			for (File file : listOfFiles) {
			    if (file.isFile() && file.getName().contains(".fit")) {
			    	countbabel++;
			    	textDesc.setText("Archivo " + countbabel);
			    	String startGPS = file.getName().replace("-", "");
			    	startGPS = startGPS.replace(".fit", "");
			    	
			    	//1:30 MIN MENOS
			    	/*
					SimpleDateFormat fixTimeFormater = new SimpleDateFormat("yyyyMMddHHmmss");
					try {
						Date dateTime = fixTimeFormater.parse(startGPS);
						Calendar calTime = Calendar.getInstance();
						
						calTime.setTime(dateTime);
						calTime.add(Calendar.SECOND, -40);
						
						startGPS = fixTimeFormater.format(calTime.getTime());
					}
					catch (ParseException e) { e.printStackTrace(); }
			    	*/

			        try {
			        	String garmin = "C:/SISTEMA/gps/gpsbabel.exe -t -i garmin_fit -f " + file.getAbsolutePath() + " -x track,faketime=f" + startGPS + "+5 -o gpx -F " + destino + "/" + file.getName().replace(".fit", ".gpx");
						Process runProcess = Runtime.getRuntime().exec("cmd /c " + garmin);
						runProcess.waitFor();
					}
			        catch (IOException | InterruptedException e) { e.printStackTrace(); }
			    	
			        gpxList.add(new File(destino + "/" + file.getName().replace(".fit", ".gpx")));
			        
			        
			    	
			    	/*
			    	try {
			    		Path path = Paths.get(file.getAbsolutePath());
						BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
						
						String[] fechaHora = String.valueOf(attr.creationTime()).split("T");
						String[] diaMesAnio = fechaHora[0].split("-");
						String[] horaMinSeg = fechaHora[1].split(":");
						String[] segFix = horaMinSeg[2].split("\\.");
						
						System.out.println("VALOR REAL: " + attr.creationTime());
						
						System.out.println();
						
						System.out.println("DIA: " + diaMesAnio[2]);
						System.out.println("MES: " + diaMesAnio[1]);
						System.out.println("AÑO: " + diaMesAnio[0]);
						System.out.println();
						System.out.println("HORA: " + horaMinSeg[0]);
						System.out.println("MINUTOS: " + horaMinSeg[1]);
						System.out.println("SEGUNDOS: " + segFix[0]);
						
						System.out.println();
						
						String inputStr = fechaHora[0] + "-" + horaMinSeg[0] + "-" + horaMinSeg[1] + "-" + segFix[0];
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
						Date inputDate = dateFormat.parse(inputStr);
						dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-6"));

						System.out.println(dateFormat.format(inputDate.getTime()));
						
					}
			    	catch (IOException | ParseException e) { e.printStackTrace(); }
			    	*/
			    	
			        /*
			        try {
			        	String garmin = "C:/SISTEMA/gps/gpsbabel.exe -t -i garmin_fit -f " + file.getAbsolutePath() + " -x track,faketime=f20160118090824+5 -o gpx -F " + System.getProperty("user.home") + "/Desktop/test2.gpx";
						@SuppressWarnings("unused")
						Process runProcess = Runtime.getRuntime().exec("cmd /c " + garmin);
					}
			        catch (IOException e) { e.printStackTrace(); }
			        */
			    }
			}
			
	        List<Double> latTmp = new ArrayList<Double>();
	        List<Double> lonTmp = new ArrayList<Double>();
	        List<String> timeTmp = new ArrayList<String>();
			
			//MERGE DE GPX SI TIENEN CONTENIDO
	        Platform.runLater( () -> text.setText("UNIENDO ARCHIVOS GPS"));
			Collections.sort(gpxList);
			for (int i=0;i<gpxList.size();i++) {
				textDesc.setText("Archivo " + (i+1) + " de " + gpxList.size());
				// System.out.println("----------------------------------");
				// System.out.println("-  " + gpxList.get(i).getName() + "  -");
				// System.out.println("----------------------------------");
				try {
					FileReader reader = new FileReader(gpxList.get(i).getAbsolutePath());
		            BufferedReader bufferedReader = new BufferedReader(reader);
		 
		            String line;
		 
		            int count = 0;
		            boolean hasInfo = false;

		            while ((line = bufferedReader.readLine()) != null) {
		            	count++;
		            	if (count == 4 && line.contains("<bounds")) { hasInfo = true; }
		            	if (count > 6 && hasInfo) {
		            		if (!line.contains("</trkseg>") && !line.contains("</trk>") && !line.contains("</gpx>") && !line.contains("</trkpt>")) {
		            			if (line.contains("      <trkpt ")) {
		            				line = line.replace("      <trkpt lat=\"", "");
		            				line = line.replace("\">", "");
		            				String[] parts = line.split("\" lon=\"");

		            				latTmp.add(Double.valueOf(parts[0]));
		            				lonTmp.add(Double.valueOf(parts[1]));
		            				// System.out.println("LAT: " + parts[0] + " - LON: " + parts[1]);
		            			}
		            			else if (line.contains("        <time>")) {
		            				line = line.replace("        <time>", "");
		            				line = line.replace("</time>", "");

		            				timeTmp.add(line);
		            				// System.out.println("TIME: " + line);
		            			}
		            		}
		            	}
		            }
		            reader.close();
				}
				catch (IOException e) { e.printStackTrace(); }
			}
			
			//PASO DE 5SEG A 1SEG EL TRACK
			Platform.runLater( () -> text.setText("GENERANDO TRACKS"));
			Platform.runLater( () -> textDesc.setText(""));
	        List<Double> latFinal = new ArrayList<Double>();
	        List<Double> lonFinal = new ArrayList<Double>();
	        List<String> timeFinal = new ArrayList<String>();
	        
			for (int i=0;i<latTmp.size();i++) {
				if ((i+1) < latTmp.size()) {
					Double difLat = latTmp.get(i) - latTmp.get(i+1);
					Double difLon = lonTmp.get(i) - lonTmp.get(i+1);
					Double miniTracksLat = difLat / 5;
					Double miniTracksLon = difLon / 5;
					
					latFinal.add(latTmp.get(i));
					lonFinal.add(lonTmp.get(i));
					timeFinal.add(timeTmp.get(i));
					
					// NUEVOS DATOS DE LAT
					
					if (difLat < 0) {
						for (int j=1;j<5;j++) { latFinal.add(latTmp.get(i) - (miniTracksLat * j)); }
					}
					else {
						for (int j=1;j<5;j++) { latFinal.add(latTmp.get(i) + (miniTracksLat * j)); }
					}
					/*
					if (miniTracksLat < 0) {
						for (int j=1;j<5;j++) { latFinal.add(latTmp.get(i) - (miniTracksLat * j)); }
					}
					else {
						for (int j=1;j<5;j++) { latFinal.add(latTmp.get(i) - (miniTracksLat * j)); }
					}
					
					if (miniTracksLon < 0) {
						for (int j=1;j<5;j++) { lonFinal.add(lonTmp.get(i) - (miniTracksLon * j)); }
					}
					else {
						for (int j=1;j<5;j++) { lonFinal.add(lonTmp.get(i) - (miniTracksLon * j)); }
					}
					*/

					// NUEVOS DATOS DE LON
					
					if (difLon < 0) {
						for (int j=1;j<5;j++) { lonFinal.add(lonTmp.get(i) - (miniTracksLon * j)); }
					}
					else {
						for (int j=1;j<5;j++) { lonFinal.add(lonTmp.get(i) + (miniTracksLon * j)); }
					}
					
					/*
					if (lonTmp.get(i) < 0 && miniTracksLon < 0) {
						for (int j=1;j<5;j++) { lonFinal.add(lonTmp.get(i) + (miniTracksLon * j)); }
					}
					else if (lonTmp.get(i) < 0 && miniTracksLon > 0) {
						for (int j=1;j<5;j++) { lonFinal.add(lonTmp.get(i) + (miniTracksLon * j)); }
					}
					else if (lonTmp.get(i) > 0 && miniTracksLon < 0) {
						for (int j=1;j<5;j++) { lonFinal.add(lonTmp.get(i) + (miniTracksLon * j)); }
					}
					else if (lonTmp.get(i) > 0 && miniTracksLon > 0) {
						for (int j=1;j<5;j++) { lonFinal.add(lonTmp.get(i) + (miniTracksLon * j)); }
					}
					*/

					//NUEVOS DATOS DE TIME
					SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
					try {
						Date dateTime = curFormater.parse(timeTmp.get(i));
						Calendar calTime = Calendar.getInstance();
						
						for (int j=1;j<5;j++) {
							calTime.setTime(dateTime);
							calTime.add(Calendar.SECOND, j);
							timeFinal.add(curFormater.format(calTime.getTime()));
						}
					}
					catch (ParseException e) { e.printStackTrace(); }
				}
			}
			
			//CREO LOS NUEVOS GPX
			Platform.runLater( () -> text.setText("CREANDO GPS POR VIDEO"));
			
			File folderVideo = new File(origen + "DCIM/100_VIRB/");
			File[] listOfFilesVideos = folderVideo.listFiles();

			boolean start = false;

			for (File file : listOfFilesVideos) {
				if (file.getName().contains(".MP4")) {
					Platform.runLater( () -> textDesc.setText("Creando " + file.getName().toUpperCase().replace(".MP4", ".GPX")));
					
					FileWriter fw;
					try {
						fw = new FileWriter(destino + "/" + file.getName().toLowerCase().replace(".mp4", ".gpx"));
						
						fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>");
						fw.write("<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:gpxx=\"http://www.garmin.com/xmlschemas/GpxExtensions/v3\" xmlns:gpxtrkx=\"http://www.garmin.com/xmlschemas/TrackStatsExtension/v1\" xmlns:gpxtrkoffx=\"http://www.garmin.com/xmlschemas/TrackMovieOffsetExtension/v1\" xmlns:wptx1=\"http://www.garmin.com/xmlschemas/WaypointExtension/v1\" xmlns:gpxtpx=\"http://www.garmin.com/xmlschemas/TrackPointExtension/v1\" xmlns:gpxpx=\"http://www.garmin.com/xmlschemas/PowerExtension/v1\" xmlns:gpxacc=\"http://www.garmin.com/xmlschemas/AccelerationExtension/v1\" creator=\"VIRB Elite\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
						fw.write("<metadata>");
						fw.write("<link href=\"http://www.garmin.com\">");
						fw.write("<text>Garmin International</text>");
						fw.write("</link>");
						fw.write("<time>2016-02-01T19:42:16Z</time>");
						fw.write("</metadata>");
						fw.write("<trk>");
						fw.write("<name>2016-02-01 15:03:05</name>");
						fw.write("<link href=\"\\DCIM\\101_VIRB\\" + file.getName().toUpperCase() + "\"/>");
						fw.write("<extensions>");
						fw.write("<gpxx:TrackExtension>");
						fw.write("<gpxx:DisplayColor>Cyan</gpxx:DisplayColor>");
						fw.write("</gpxx:TrackExtension>");
						fw.write("<gpxtrkoffx:TrackMovieOffsetExtension>");
						fw.write("<gpxtrkoffx:StartOffsetSecs>0.774</gpxtrkoffx:StartOffsetSecs>");
						fw.write("</gpxtrkoffx:TrackMovieOffsetExtension>");
						fw.write("</extensions>");
						fw.write("<trkseg>");
						
						//BUSCO EL TIME
						Path filePath = Paths.get(file.getAbsolutePath());
						BasicFileAttributes attr = Files.readAttributes(filePath, BasicFileAttributes.class);
						
						String[] fechaHora = String.valueOf(attr.creationTime()).split("T");
						//String[] diaMesAnio = fechaHora[0].split("-");
						String[] horaMinSeg = fechaHora[1].split(":");
						String[] segFix = horaMinSeg[2].split("\\.");
						
						String inputStr = fechaHora[0] + "-" + horaMinSeg[0] + "-" + horaMinSeg[1] + "-" + segFix[0];
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
						Date inputDate = dateFormat.parse(inputStr);
						dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-6"));

						String[] dateMP4 = dateFormat.format(inputDate.getTime()).split("-");
						
						for (int i=0;i<timeFinal.size();i++) {
							String[] fechaHoraGPS = String.valueOf(timeFinal.get(i)).split("T");
							String[] diaMesAnioGPS = fechaHoraGPS[0].split("-");
							String[] horaMinSegGPS = fechaHoraGPS[1].split(":");
							String[] segFixGPS = horaMinSegGPS[2].split("\\.");
							
							segFixGPS[0] = segFixGPS[0].replace("Z", "");
							
							if (
								dateMP4[0].equals(diaMesAnioGPS[0]) &&
								dateMP4[1].equals(diaMesAnioGPS[1]) &&
								dateMP4[2].equals(diaMesAnioGPS[2]) &&
								dateMP4[3].equals(horaMinSegGPS[0]) &&
								dateMP4[4].equals(horaMinSegGPS[1]) &&
								dateMP4[5].equals(segFixGPS[0])
								)
							{ start = true; }

							if (start) {
								fw.write("<trkpt lat=\"" + latFinal.get(i) + "\" lon=\"" + lonFinal.get(i) + "\">");
								fw.write("<ele>25.62</ele>");
								fw.write("<time>" + timeFinal.get(i) + "</time>");
								fw.write("<extensions>");
								fw.write("<gpxacc:AccelerationExtension>");
								fw.write("<gpxacc:accel offset=\"26\" x=\"0.1\" y=\"0.7\" z=\"-0.8\"/>");
								fw.write("<gpxacc:accel offset=\"26\" x=\"0.1\" y=\"0.7\" z=\"-0.8\"/>");
								fw.write("<gpxacc:accel offset=\"26\" x=\"0.1\" y=\"0.7\" z=\"-0.8\"/>");
								fw.write("<gpxacc:accel offset=\"26\" x=\"0.1\" y=\"0.7\" z=\"-0.8\"/>");
								fw.write("<gpxacc:accel offset=\"26\" x=\"0.1\" y=\"0.7\" z=\"-0.8\"/>");
								fw.write("<gpxacc:accel offset=\"26\" x=\"0.1\" y=\"0.7\" z=\"-0.8\"/>");
								fw.write("<gpxacc:accel offset=\"26\" x=\"0.1\" y=\"0.7\" z=\"-0.8\"/>");
								fw.write("<gpxacc:accel offset=\"26\" x=\"0.1\" y=\"0.7\" z=\"-0.8\"/>");
								fw.write("<gpxacc:accel offset=\"26\" x=\"0.1\" y=\"0.7\" z=\"-0.8\"/>");
								fw.write("<gpxacc:accel offset=\"26\" x=\"0.1\" y=\"0.7\" z=\"-0.8\"/>");
								fw.write("</gpxacc:AccelerationExtension>");
								fw.write("</extensions>");
								fw.write("</trkpt>");
							}
						}
						fw.write("</trkseg>");
						fw.write("</trk>");
						fw.write("</gpx>");
						
						fw.close();
					}
					catch (IOException | ParseException e) { e.printStackTrace(); }
				}
				start = false;
			}
		}});
		tgps.start();
	}
}
