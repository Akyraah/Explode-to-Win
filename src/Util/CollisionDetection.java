package Util;

import java.awt.Rectangle;

public class CollisionDetection {
		
	 public static int[] Collision (Rectangle r1, Rectangle r2) {
		 	
		 	double c1X = r1.getCenterX();
		 	double c1Y = r1.getCenterY();
		 	double c2X = r2.getCenterX();
		 	double c2Y = r2.getCenterY();
		 	
		 	double l1 = r1.getWidth();
		 	double L1 = r1.getHeight();
		 	double l2 = r2.getWidth();
		 	double L2 = r2.getHeight();
		 	
		 	double dX = c1X - c2X;
		 	double dY = c1Y - c2Y;
		 	double LX = (l1 + l2) / 2;
		 	double LY = (L1 + L2) / 2;
		 	
		 	if (Math.abs(dX) < LX && Math.abs(dY) < LY) {
		 		// Il y a collision
                                System.out.println("Il y a collision à ...");
		 		double PX = LX - Math.abs(dX);
		 		double PY = LY - Math.abs(dY);

		 		if (PX < PY ){
		 			// Collision � gauche ou � droite
		 			if (dX > 0){
		 				System.out.println("Gauche : " + dX);	
		 				
		 				//Collision = COLLISION_A_DROITE du bloc;
		 				return new int[] {(int) PX, 0};
		 			}
		 			else{
		 				System.out.println("Droite : " +dX);
		 				
		 				//Collision = COLLISION_A_GAUCHE du bloc;
		 				return new int[] {(int )-PX, 0};
		 			}
		 		}
		 		else {
		 			// Collision en haut ou en bas
		 			if (dY > 0){
		 				System.out.println("Bas");

		 				//Collision = COLLISION_EN_BAS du bloc;
		 				return new int[] {0, (int) PY};
		 			}
		 			else{
		 				System.out.println("Haut");

		 				//Collision = COLLISION_EN_HAUT du bloc;
		 				return new int[] {0, (int) -PY};
		 			}
		 		}
		 	}
		 	
		 	
		 	return new int[] {0, 0};
		 	
		 	
	}
        
         
         public static boolean Collision2 (Rectangle r1, Rectangle r2, int dX, int dY){
             
            Rectangle r1Prime = new Rectangle(r1.x + dX,r1.y +dY, r1.width,r1.height);
            
             return r1Prime.intersects(r2);
         }
         
	 public static boolean Sol (Rectangle r1, Rectangle r2) {
		 	double c1X = r1.getCenterX();
		 	double c1Y = r1.getCenterY();
		 	double c2X = r2.getCenterX();
		 	double c2Y = r2.getCenterY();
		 	
		 	double l1 = r1.getWidth();
		 	double L1 = r1.getHeight();
		 	double L2 = r2.getHeight();
                        
		 	double LY = (L1 + L2) / 2;
		 	
		 	if (Math.abs(c1X-c2X) < ((r1.width + r2.width) / 2) && c2Y > c1Y){
		 		if ((c2Y - c1Y) - 3 < LY){
		 			return true;
		 					
		 		}
		 	}
		 	return false;
	 }
}
