#include <WiFi.h>
#include <FirebaseESP32.h>
#include <NTPClient.h>
#include <LiquidCrystal_I2C.h>
#include <time.h>

/*Define information of WiFi*/ 
#define WIFI_SSID "Phong 4"
#define WIFI_PASSWORD "*578284*"
/*End*/

/* Declare auth and host of firebase */
#define FIREBASE_HOST "iot-khoaluan-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH "JdGaxGCCGFakJpu7cmWHqdbfoy64FXp2BVAUNMNC"
/*End*/

/*Declare path for firebase */
String path = "/sensor2/Present";
String path1 = "/sensor2/History";
FirebaseData firebaseData;
/*End*/

/*Variable for pH*/
const float calibration_value = 19.24;
const int BUFFER_SIZE = 10;
int buffer_arr[BUFFER_SIZE], temp;
unsigned int avgval;
float ph_act;
/*End*/

/*Declare for lcd*/
LiquidCrystal_I2C lcd(0x27, 16, 2);
/*End*/

/*Variable for Water flow Sensor*/
#define sensorPin 5
long currentMillis = 0;
long previousMillis = 0;
int interval = 1000;
boolean ledState = LOW;
float calibrationFactor = 4.5;
volatile byte pulseCount;
byte pulse1Sec = 0;
float flowRate;
unsigned long flowMilliLitres;
unsigned int totalMilliLitres;
float flowLitres;
float totalLitres;
float hisWater = 0;
void IRAM_ATTR pulseCounter()
{
  pulseCount++;
}
/*End*/

/*Variable for TDS sensor*/
#define TdsSensorPin 32
int ADC_value;
unsigned long int avgval_ADC;
int buffer_tds[10],temp1;
float TDS = 0;
/*End TDS*/


/*Variable for real-time*/
int timezone = 7*3600;
int dst = 0;
/*End*/

void setup() {
  // put your setup code here, to run once:
 Serial.begin(500000);
  pinMode(TdsSensorPin,INPUT);
  pinMode(sensorPin, INPUT_PULLUP);

  //  Set up lcd
  lcd.begin();
  lcd.clear();
  lcd.backlight();

   /*Begin the conection of wifi*/
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    while(WiFi.status() != WL_CONNECTED){
      delay(500);
      Serial.print(".");
    }

    Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
    Firebase.reconnectWiFi(true);
    if(!Firebase.beginStream(firebaseData, path)){
      Serial.println("REASON: " + firebaseData.errorReason());
      Serial.println();
    }
    Serial.print("Connected with IP:");
    Serial.println(WiFi.localIP());
    Serial.println();

    configTime(timezone, dst, "pool.ntp.org", "time.nist.gov");
    Serial.println("Waiting for server");
    while(!time(nullptr)){
      Serial.print("#");
      delay(500);
    }
    Serial.print("Time respone is ok");
    pulseCount = 0;
    flowRate = 0.0;
    flowMilliLitres = 0;
    totalMilliLitres = 0;
    previousMillis = 0;
    attachInterrupt(digitalPinToInterrupt(sensorPin), pulseCounter, FALLING);
}

void loop() {
  // put your main code here, to run repeatedly:
currentMillis = millis();
    if (currentMillis - previousMillis > interval) 
    {
        pulse1Sec = pulseCount;
        pulseCount = 0;
 
        flowRate = ((1000.0 / (millis() - previousMillis)) * pulse1Sec) / calibrationFactor;
        previousMillis = millis();
 
        flowMilliLitres = (flowRate / 60) * 1000;
        flowLitres = (flowRate / 60);
 
        // Add the millilitres passed in this second to the cumulative total
        totalMilliLitres += flowMilliLitres;
        totalLitres      += flowLitres;
  	}
    //Ð?c c?m bi?n do TDS
	for(int i=0;i<10;i++) { 
    	buffer_tds[i]=analogRead(TdsSensorPin);
    	delay(30);
  	}

  	for(int i=0;i<9;i++) {
    	for(int j=i+1;j<10;j++) {
      		if(buffer_tds[i]>buffer_tds[j]) {
        		temp1 = buffer_tds[i];
        		buffer_tds[i] = buffer_tds[j];
        		buffer_tds[j] = temp1;
      		}
    	}
  	}

  	avgval_ADC  = 0;
  	for(int i = 2; i < 8; i++) {
    	avgval_ADC += buffer_tds[i];
  	}
      const float voltage_value = (float)avgval_ADC*3.3/4096/6;
      if(voltage_value != 0){
        TDS = (133.42/voltage_value*voltage_value-255.86*voltage_value*voltage_value + 857.39*voltage_value)*0.5;
      }
  	// Ð?c c?m bi?n do pH
   	for (int i = 0; i < 10; i++) { 
    	buffer_arr[i] = analogRead(35);
    	delay(30);
  	}

  	for (int i = 0; i < 9; i++) {
    	for (int j = i + 1; j < 10; j++) {
      		if (buffer_arr[i] > buffer_arr[j]) {
        		temp = buffer_arr[i];
        		buffer_arr[i] = buffer_arr[j];
        		buffer_arr[j] = temp;
      		}
    	}
  	}

  	avgval = 0;
  	for (int i = 2; i < 8; i++) {
    	avgval += buffer_arr[i];
  	}

  	const float volt = (float) avgval * 3.3 / 4096.0 / 6;
  	ph_act = -5.70 * volt + calibration_value;
 
    time_t now = time(nullptr);
    struct tm* p_tm = localtime(&now);
    if(	p_tm ->tm_mon + 1 == 1 || p_tm ->tm_mon + 1 == 3 || p_tm ->tm_mon + 1 == 5 || p_tm ->tm_mon + 1 == 7 || p_tm ->tm_mon + 1 == 8 || p_tm ->tm_mon + 1 == 10 || 	p_tm ->tm_mon + 1 == 12)
	   		{
	   			if(p_tm ->tm_mday == 31 && p_tm ->tm_hour == 23 && p_tm ->tm_min == 59 && p_tm ->tm_sec == 59){
	   				hisWater = totalLitres;
	   				totalLitres = 0;
	   				if(p_tm ->tm_mon + 1 == 1){
	   					Firebase.setInt(firebaseData, path1 + "/1/moth",p_tm ->tm_mon + 1);
	   					Firebase.setFloat(firebaseData, path1 + "/1/val",hisWater);
					}else if(p_tm ->tm_mon + 1 == 3){
						Firebase.setInt(firebaseData, path1 + "/3/moth",p_tm ->tm_mon + 1);
	   					Firebase.setFloat(firebaseData, path1 + "/3/val",hisWater);
					}else if(p_tm ->tm_mon + 1 == 5){
						Firebase.setInt(firebaseData, path1 + "/5/moth",p_tm ->tm_mon + 1);
	   					Firebase.setFloat(firebaseData, path1 + "/5/val",hisWater);
					}
					else if(p_tm ->tm_mon + 1 == 7){
						Firebase.setInt(firebaseData, path1 + "/7/moth",p_tm ->tm_mon + 1);
	   					Firebase.setFloat(firebaseData, path1 + "/7/val",hisWater);
					}else if(p_tm ->tm_mon + 1 == 8){
						Firebase.setInt(firebaseData, path1 + "/8/moth",p_tm ->tm_mon + 1);
	   					Firebase.setFloat(firebaseData, path1 + "/8/val",hisWater);
					}else if(p_tm ->tm_mon + 1 == 10){
						Firebase.setInt(firebaseData, path1 + "/10/moth",p_tm ->tm_mon + 1);
	   					Firebase.setFloat(firebaseData, path1 + "/10/val",hisWater);
					}else if(p_tm ->tm_mon + 1 == 12){
						Firebase.setInt(firebaseData, path1 + "/12/moth",p_tm ->tm_mon + 1);
	   					Firebase.setFloat(firebaseData, path1 + "/12/val",hisWater);
					}
				}
			}
		else if(p_tm ->tm_mon + 1 == 4 || p_tm ->tm_mon + 1 == 6 || p_tm ->tm_mon + 1 == 9 || p_tm ->tm_mon + 1 == 11){
			if(p_tm ->tm_mday == 30 && p_tm ->tm_hour == 23 && p_tm ->tm_min == 59 && p_tm ->tm_sec == 59){
				hisWater = totalLitres;
	   			totalLitres = 0;
	   			if(p_tm ->tm_mon + 1 == 4){
	   				Firebase.setInt(firebaseData, path1 + "/4/moth",p_tm ->tm_mon + 1);
	   				Firebase.setFloat(firebaseData, path1 + "/4/val",hisWater);
				}else if(p_tm ->tm_mon + 1 == 6){
					Firebase.setInt(firebaseData, path1 + "/6/moth",p_tm ->tm_mon + 1);
	   				Firebase.setFloat(firebaseData, path1 + "/6/val",hisWater);
				}else if(p_tm ->tm_mon + 1 == 9){
					Firebase.setInt(firebaseData, path1 + "/9/moth",p_tm ->tm_mon + 1);
	   				Firebase.setFloat(firebaseData, path1 + "/9/val",hisWater);
				}
				else if(p_tm ->tm_mon + 1 == 11){
					Firebase.setInt(firebaseData, path1 + "/11/moth",p_tm ->tm_mon + 1);
	   				Firebase.setFloat(firebaseData, path1 + "/11/val",hisWater);
				}
			}
		}else if(p_tm ->tm_mon + 1 == 2){
			if(p_tm ->tm_mday == 29 && p_tm ->tm_hour == 23 && p_tm ->tm_min == 59 && p_tm ->tm_sec == 59){
				hisWater = totalLitres;
	   			totalLitres = 0;
	   			Firebase.setInt(firebaseData, path1 + "/2/moth",p_tm ->tm_mon + 1);
	   			Firebase.setFloat(firebaseData, path1 + "/2/val",hisWater);
			}
		}
  	String time = String(p_tm->tm_mday) + "/" + String(p_tm->tm_mon + 1) + "/" + String(p_tm->tm_year + 1900) + " " + String(p_tm->tm_hour) + ":" +  String(p_tm->tm_min) + ":" + String(p_tm->tm_sec);

  	Firebase.setFloat(firebaseData, path + "/total", (float)totalLitres);
	  Firebase.setFloat(firebaseData, path + "/speed", (float)flowRate);
  	Firebase.setFloat(firebaseData, path + "/ph", (float)ph_act);
  	Firebase.setFloat(firebaseData, path + "/tds", (float)TDS);
  	Firebase.setString(firebaseData, path + "/time", time);
  	Serial.print("pH Val: ");
  	Serial.print(ph_act);
  	Serial.print(" Total: ");
  	Serial.println(totalLitres);

  	lcd.setCursor(0,0);
  	lcd.print("Tot");
  	lcd.setCursor(0,1);
  	lcd.print(totalLitres, 2);
  	lcd.setCursor(5,0);
  	lcd.print("TDS");
  	lcd.setCursor(5,1);
  	lcd.print(TDS, 0);
  	lcd.setCursor(11,0);
  	lcd.print("pH");
  	lcd.setCursor(11,1);
  	lcd.print(ph_act);
}
