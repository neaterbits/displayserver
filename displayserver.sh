CLASSPATH=`find -name "*.jar" | grep -v sources | tr '\n' ':'`

java -cp $CLASSPATH com.neaterbits.displayserver.main.DisplayServerMain $@
