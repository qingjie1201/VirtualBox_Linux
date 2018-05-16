cd kalix-parent && mvn clean install
cd ..

cd framework-parent && mvn -DskipTests=true clean install
cd ..

cd admin-parent && mvn  -DskipTests=true clean install
cd ..

cd middleware-parent && mvn  -DskipTests=true clean install
cd ..

cd oa-parent && mvn clean install
cd ..

cd common-parent && mvn clean install
cd ..

cd schedule-parent && mvn clean install
cd ..

cd research-parent && mvn clean install
cd ..

cd art-parent && mvn clean install
cd ..

cd tools-parent && mvn clean install
cd ..
