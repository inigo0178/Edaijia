#include "edlib.h"
#include <stdio.h>


JNIEXPORT jstring JNICALL Java_cn_edaijia_android_client_EDriverApp_get
  (JNIEnv * env, jclass cla, jint i){
	char str[] = "10047f3db98-eaa5-11e1-92bc-00163e0107dd86";
	char *pstr = str;
	pstr += 3;
	pstr[strlen(pstr) - 2] = '\0';

	jstring encoding; 	
	switch(i){
	case 1:
	//Base_URL
	encoding = (*env)->NewStringUTF(env,"http://api.edaijia.cn/rest/");
	break;
	case 2:
	//MD5
	encoding = (*env)->NewStringUTF(env,pstr);
	break;
	//WEIBO_KEY
	case 3:
	encoding = (*env)->NewStringUTF(env,"1864959196");
	break;
	//WEIBO_SECRET
	case 4:
	encoding = (*env)->NewStringUTF(env,"c5a44c8e9f90cae911b932689f04529f");
	break;
	case 5:
	//WEIBO_URL
	encoding = (*env)->NewStringUTF(env,"http://www.edaijia.cn/v2/index.php?r=site/callback&app=sina");
	break;
	case 6:
	//BAIDU_MAO_KEY
	encoding = (*env)->NewStringUTF(env,"0DC81D7627502AFB2F05AC30F678EEF4EDF61675");
	break;
	default:
	break;
	}
	return encoding; 
}
