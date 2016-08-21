package FaceRec;
import org.opencv.android.OpenCVLoader;
import android.util.Log;
public class SesFaceRec {
	static{
		try {
			loadLibOk=false;
			Log.e("log", "libs load ...");
	
			System.loadLibrary("gnustl_shared");
		
			System.loadLibrary("opencv_java");
			System.loadLibrary("SES_FaceRec");
			Log.e("log", "libs load ok!");

			if (OpenCVLoader.initDebug()) {

				loadLibOk = true;
			}
		} catch (Exception e) {

			Log.e("Translator", e.getMessage());
		}
		Log.e("FaceNormal", "loadLibrary TestFaceNormal....END");

	}
	
	
	
	
	
	public void init(String dir){
		try {
			if(!loadLibOk){
				loadSuccess = false;
				return;
			}
			//GloabalVar.FaceData_Dir
			int ret = this.sfrInit(dir);// ��ʼ����Ŀ���Ǽ���ѧϰ�⣬ֻ���ʼ��һ��
			// ��ʼ���ٶ�����
			Log.e("log", "initialize done");
			loadSuccess = true;
		} catch (Exception e) {
			loadSuccess=false;
			e.printStackTrace();
		}
		
	}
	

	public static boolean loadLibOk;
	public static boolean loadSuccess;
	public int sFRInitFlag;
	

	/* 01 ��ʼ�� */
	public native int sfrInit(String initDir);

	/* 02 ж�� */
	public native void sfrUninit();

	/* 03 ��ȡָ���ļ�Asm���� */
	public native float sfsGetFaceSimilarityByPath(String SrcFilePath1,
			int SexId1, String SrcFilePath2, int SexId2);
}