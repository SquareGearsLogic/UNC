# LibreOffice Vertical Boustrophedon Extension

Concept/Alpha version, single column supported, no boustrophedon yet...

## For Developers:

1. Install [LibreOffice](http://www.libreoffice.org/download) & the [LibreOffice SDK](http://www.libreoffice.org/download) (5.0 or greater)
2. Install [Eclipse](http://www.eclipse.org/) IDE for Java Developers & the [LOEclipse plugin](https://marketplace.eclipse.org/content/loeclipse)
3. git clone https://github.com/SquareGearsLogic/UNC.git
4. Import the project in Eclipse (File->Import->Existing Projects into Workspace)
5. Let Eclipse know the paths to LibreOffice & the SDK (Project->Properties->LibreOffice Properties)
6. Setup Run Configuration
    * Go to Run->Run Configurations
    * Create a new run configuration of the type "LibreOffice Application"
    * Select the project
    * Run!
    * *Hint: Show the error log to view the output of the run configuration (Window->Show View->Error Log)*
7. The extension will be installed in LibreOffice (see Tools->Extension Manager)
8. To toggle Boustrophedon mode, click on the newly added toolbar/menu entry which have been added to Writer (named "Tools -> Add-ons -> Boustrophedon -> Toggle").

## License:
Apache License Version 2.0, January 2004