package Util;

import java.awt.Point;
import java.util.ArrayList;

public class FogNode {
	public int x;
	public int y;
	public ArrayList<FogNode> leafList;
	public FogNode(){
		leafList = new ArrayList<FogNode>();
		x = 0;
		y = 0;
	}
	
	public boolean contains(Point point){
		for (int i = 0; i < leafList.size(); i++){
			if (leafList.get(i).x == point.x && leafList.get(i).y == point.y){
				return true;
			}
		}
		return false;
	}
	
	public FogNode addToEnd(Point point){
		for (int i = 0; i < leafList.size(); i++){
			if (leafList.get(i).x == point.x && leafList.get(i).y == point.y){
				return leafList.get(i);
			}
		}
		FogNode fogNode = new FogNode();
		fogNode.x = point.x;
		fogNode.y = point.y;
		leafList.add(fogNode);
		return fogNode;
		
	}
	
	public int numberOfLeaves(){
		int sum = 0;
		for (int i = 0; i < leafList.size(); i++){
			sum += leafList.get(i).numberOfLeaves();
		}
		return 1 + sum;
	}
}
