import java.io.Serializable;

import org.opencv.core.Point;
import org.opencv.features2d.KeyPoint;

public class SerializedKP implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4783000988799865232L;
	double x;
	double y;
	float 	_size;
	float 	_angle;
	float 	_response;
	int 	_octave;
	int 	_class_id;
	public SerializedKP(KeyPoint k){
		x = k.pt.x;
		y = k.pt.y;
		_size = k.size;
		_angle = k.angle;
		_response = k.response;
		_octave = k.octave;
		_class_id = k.class_id;
	}
	public KeyPoint toKeyPoint(){
		KeyPoint k = new KeyPoint();
		k.pt = new Point(x,y);
		k.size = _size;
		k.angle = _angle;
		k.response = _response;
		k.octave = _octave;
		k.class_id = _class_id;
		return k;
	}
}
