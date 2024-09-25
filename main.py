import os
bash = "#!/bin/bash\n"
bash += 'git pull\n'
bash += 'cd Server/src\n'
b_cmd = "javac -cp ."
libs = ""
for filename in os.listdir(os.getcwd() + "/JavaPlugins"):
    libs += f":../../JavaPlugins/" + filename
b_cmd += libs + " -d ../bin Main.java"
bash += b_cmd + "\n"
bash += "cd ../bin\n"
r_cmd = "java -cp ." + libs + " Main"
bash += r_cmd + "\n"
with open("server.sh", 'w') as f:
    f.write(bash)