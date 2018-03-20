# LicencePlateRecognition
Bu uygulama 2017 yılında [Aktek Bilgi Iletisim Teknolojisi Sanayi ve Ticaret A.S.](http://www.aktekbilisim.com/tr-tr/Pages/default.aspx) firmasında yaz stajının 3 haftasında geliştirdiğım bir uygulamadır.

Uygulamanın amacı [MOVERİO PRO BT-2000](https://www.epson.com.tr/products/see-through-mobile-viewer/moverio-pro-bt-2000) cihazında çalışan gerçek zamanlı olarak araç plakalarını tanıyarak okuması ve bu plakanın emniyet müdürlüğü veritabanında şüpheli olarak aranıp aranmadığını tespit ederek kullanıcıyı bilgilendirmesidir. 

Ben stajım bitene kadar tespit ve okuma işlemini yapabildim. Büyük oranda doğru sonuç elde ediyoruz. Sorgulama işlemi bu uygulama içinde yoktur.

![MOVERİO PRO BT-2000](https://www.epson.com.tr/files/assets/converted/510m-310m/1/5/2/b/152b_03_09-.png.png)


Bu uygulamada [OpenCV](https://opencv.org/releases.html) ve [tess-two](https://github.com/rmtheis/tess-two) kullandım.

- OpenCV yi Anroid Studio ya ekleme:
![OpenCV](https://github.com/satilmisyusuf/LicencePlateRecognition/blob/master/images/Resim1.png)

- tess-two kütüphanesini Anroid Studio ya ekleme:
![tess-two](https://github.com/satilmisyusuf/LicencePlateRecognition/blob/master/images/Resim2.png)


Dikkat edilmesi gerekenler:
-Plaka tespit edildikten sonra OCR işlemi için yazının yatay düzleme paralel olması gerekmektedir. (Resimdeki gibi kesme işlemi yapılırsa plaka okunamayacaktır.)


![egik plaka](https://github.com/satilmisyusuf/LicencePlateRecognition/blob/master/images/Resim3_res.png)

- Kesme işlemi düzgün yapıldıktan sonra o resmi binary şekle çevrimemiz gerekir.


![resim4](https://github.com/satilmisyusuf/LicencePlateRecognition/blob/master/images/Resim4_res.png)    -->   ![resim5](https://github.com/satilmisyusuf/LicencePlateRecognition/blob/master/images/Resim5_res.png)


Uygulama tamamlandıktan sonra test ettik.


![resim6](https://github.com/satilmisyusuf/LicencePlateRecognition/blob/master/images/Resim6.png)

![resim7](https://github.com/satilmisyusuf/LicencePlateRecognition/blob/master/images/Resim7.png)

![resim8](https://github.com/satilmisyusuf/LicencePlateRecognition/blob/master/images/Resim9.png)
