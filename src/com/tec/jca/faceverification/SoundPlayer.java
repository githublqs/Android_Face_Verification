package com.tec.jca.faceverification;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Build;

public class SoundPlayer {
	private SoundPool soundPool;
	private HashMap<Integer, Integer> soundPoolMap;
	private Context context;
	public SoundPlayer(Context context) {
		this.context=context;
		/*soundPool= new SoundPool(10,AudioManager.STREAM_SYSTEM,5);
	   	*/
		initSoundPool();
	}
	public boolean needPlay(){
		return ParamStorage.getInstance(FaceVerificationApplication.getSingleInstance()).isHas_hint_sound();
	}
	private void initSoundPool(){
        /**
         * 21�汾��SoundPool�Ĵ��������ܴ�ı�
         */
        //�ж�ϵͳsdk�汾������汾����21�����õ�һ��
        if(Build.VERSION.SDK_INT>=21){
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(9);//������Ƶ����
            //AudioAttributes��һ����װ��Ƶ�������Եķ���
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);//������Ƶ���ĺ��ʵ�����
            builder.setAudioAttributes(attrBuilder.build());//����һ��AudioAttributes
            soundPool = builder.build();
        }else{
        	soundPool = new SoundPool(9,AudioManager.STREAM_MUSIC,0);
        }
        //load�ķ���ֵ��һ��int���ֵ����Ƶ��id����SoundPool��play()�����м������id���ܲ��������Ƶ
        soundPoolMap = new HashMap<Integer, Integer>();

		soundPoolMap.put(1, soundPool.load(context, R.raw.numer_1, 1));
		soundPoolMap.put(2, soundPool.load(context, R.raw.numer_2, 1));
		soundPoolMap.put(3, soundPool.load(context, R.raw.numer_3, 1));
		soundPoolMap.put(100, soundPool.load(context, R.raw.kuaimen, 1));
		
		soundPoolMap.put(101, soundPool.load(context, R.raw.qzb, 1));//��׼��
		soundPoolMap.put(102, soundPool.load(context, R.raw.jjcxps, 1));//������������
		soundPoolMap.put(103, soundPool.load(context, R.raw.yzbtg, 1));//��֤��ͨ��
		soundPoolMap.put(104, soundPool.load(context, R.raw.yztg, 1));//��֤ͨ��
		soundPoolMap.put(105, soundPool.load(context, R.raw.yzjs, 1));//��֤����
		
		soundPoolMap.put(106, soundPool.load(context, R.raw.yzbtg, 1));//����ʧ��
		soundPoolMap.put(107, soundPool.load(context, R.raw.qzb, 1));//�����ſ���
		soundPoolMap.put(108, soundPool.load(context, R.raw.numer_3, 1));//�����ſ���
		
		/*soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {

			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				// TODO Auto-generated method stub

			}
		});*/
    }
	/*public void playCameraClick(){
		soundPool.play(1,1, 1, 1, 0, 1);
	}*/

	public void play(int number) {
		if(!needPlay())
			return;
		stopAll();
		if (number > 0 && number < 4) {
			soundPool.play(soundPoolMap.get(number), 1, 1, 1, 0, 1); 
		}
	}
	public void playKuaimenSound() {
		if(!needPlay())
			return;
		stopAll();
		soundPool.play(soundPoolMap.get(100), 1, 1, 1, 0, 1); 
	}
	//������֤ͨ��
	public void playYZTG() {
		if(!needPlay())
			return;
		stopAll();
		soundPool.play(soundPoolMap.get(104), 1, 1, 1, 0, 1); 
		
		
	}
	//������֤����
	public void playYZJS() {
		if(!needPlay())
			return;
		stopAll();
		soundPool.play(soundPoolMap.get(105), 1, 1, 1, 0, 1); 
		
	}
	//���Ų�ͨ��
	public void playYZBTG() {
		if(!needPlay())
			return;
		stopAll();
		soundPool.play(soundPoolMap.get(103), 1, 1, 1, 0, 1); 
		
	}
	//������׼��
	public void playQZB() {
		if(!needPlay())
			return;
		stopAll();
		soundPool.play(soundPoolMap.get(101), 1, 1, 1, 0, 1); 
		
	}
	//���ż�����������
	public void playJJCXPS() {
		if(!needPlay())
			return;
		stopAll();
		soundPool.play(soundPoolMap.get(102), 1, 1, 1, 0, 1); 
		
	}
	//��������ʧ��
		public void playPZSB() {
			if(!needPlay())
				return;
			stopAll();
			soundPool.play(soundPoolMap.get(106), 1, 1, 1, 0, 1); 
			
		}
		
		//���������ſ���
		public void playFaceInRect() {
			if(!needPlay())
				return;
			stopAll();
			int streamID=soundPoolMap.get(107);
			soundPool.play(streamID, 1, 1, 1, 0, 1); 
			
		}
		
		

	//�������ճɹ�����ʼ��֤����������
	public void playPZCG_start_verify() {
		if(!needPlay())
			return;
		stopAll();
		soundPool.play(soundPoolMap.get(108), 1, 1, 1, 0, 1); 
		
	}
	private void stopAll(){
		Set<Entry<Integer, Integer>> entrySet = soundPoolMap.entrySet();
		for (Entry<Integer, Integer> entry : entrySet) {
			int streamID=entry.getValue();
			soundPool.stop(streamID);
		}
	}

}
