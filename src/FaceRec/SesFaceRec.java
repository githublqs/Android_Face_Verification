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
			int ret = this.sfrInit(dir);// 初始化：目的是加载学习库，只需初始化一次
			// 初始化速度略慢
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
	

	/* 01 初始化 */
	public native int sfrInit(String initDir);

	/* 02 卸载 */
	public native void sfrUninit();

	/* 03 获取指定文件Asm坐标 */
	public native float sfsGetFaceSimilarityByPath(String SrcFilePath1,
			int SexId1, String SrcFilePath2, int SexId2);
}