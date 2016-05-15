import java.io.File;

public class zTest {


	public static void walk( String path ) {

		File root = new File( path );
		File[] list = root.listFiles();

		if (list == null) return;

		for ( File f : list ) {
			if ( f.isDirectory() ) {
				walk( f.getAbsolutePath() );
				System.out.println( "Dir:" + f.getAbsoluteFile() );
			}
			else {
				System.out.println( "File:" + f.getAbsoluteFile() );
			}
		}
	}

	public static void main(String[] args) {
	//	walk("C://opencv//workfile");
		 //C:/opencv/workfile/att_faces/s2/1.pgm;1
		for(int i = 1 ; i<=40 ; i++ ){
			for(int j = 1 ; j<=10 ; j++ ){
				System.out.println("C:/opencv/workfile/att_faces/s"+i+"/s"+i+"_"+j+".pgm;"+(i-1));
			}
			
		}
	}

}
