# Finanzverwaltung
Manage your finances (expenses, earnings and wealth) with this JAVA web application. This web application uses JSF, Primefaces and Bootstrap and provides a platform where users can register and view/edit/add/delete data entries about expenses, earnings, monthly and yearly wealth. Charts with information about expenses and earnings are shown. Data entries can be searched or exported as a PDF,XLS,CSV or XML file. Attachments for a data entry can be upload to a Nextcloud instance.

## Prerequisites
To run this application following is needed:
* A Glassfish server or Tomee server with at least JAVA JDK 7
* Postgresql Server

## Installing
* Enter DB Connection Information in File hibernate.cfg - The file is in the folder src/main/resources
* Enter URLs, username and password of a nextcloud instance in the in the variables baseUrl, downloadUrl, cloudBenutzername and cloudPasswort in the files AusgabenController, EinnahmenController, VermoegenController and VermoegenJaehrlichController. (These files are available in the path: /src/main/resources/...finanzverwaltung/controller/ )
* Recompile the project 
* Use the war file in the target folder to install the web application on the glassfish or tomee server

## Authors

* **A. Dridi** - [a-dridi](https://github.com/a-dridi/)

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

