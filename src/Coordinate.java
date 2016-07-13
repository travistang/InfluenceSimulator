public class Coordinate {
	public int x,y;
	public Coordinate(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	public Coordinate()
	{
		this.x = this.y = -1;
	}
	public Coordinate(Pair<Integer,Integer> p)
	{
		this.x = p.first;
		this.y = p.second;
	}
	public boolean isInvalid()
	{
		return x != -1 && y != -1;
	}
	@Override
	public String toString()
	{
		return "(" + x + "," + y +")";
	}
	// con = 0 refers to the uppermost corner. The rest is counted clockwise
	public Coordinate getCornorCoordinateOfHexagon(int con)
	{
		Coordinate c = new Coordinate(this.x,this.y);
		switch(con)
		{
			case 0:
				c.x += 6;
				break;
			case 1:
				c.x += 12;
				c.y += 4;
				break;
			case 2:
				c.x += 12;
				c.y += 8;
				break;
			case 3:
				c.x += 6;
				c.y += 12;
				break;
			case 4:
				c.y += 8;
				break;
			case 5:
				c.y += 4;
				break;
			default:
				break;
		}
		return c;
	}
	// check if the coordinate is a valid cell coordinate
	public static boolean isValidCellCoordinate(Coordinate c)
	{
		
		return (c.x >= 36 && c.y >= 36 								// within the boundary
				&&(c.x % 36 == 0 && (c.y - 36) % 16 == 0)	// conditions for upper row: (36 + 36n,36 + 16n)
				||((c.x - 54) % 36 == 0 && (c.y - 44) % 16 == 0)); 	// conditions for lower row: (54 + 36n, 44 + 16n) 
	}
	public static double distance(Coordinate a, Coordinate b)
	{
		return Math.sqrt((a.x - b.x)*(a.x - b.x) + (a.y - b.y)*(a.y - b.y));
	}
	// get the coordinate of an adjacent cell given the current cell is a cell coordinate
	// if the current coordinate is not a valid cell coordinate
	// the method will return the adjacent cell coordinate of given direction of the nearest cell
	// it will ensure the coordinate is either a valid cell coordinate (if it is within boundary) or null
	public Coordinate getAdjacentCellCoordinate(int dir)
	{
		if(dir < 0 || dir >= 6 )return null;
		if(!isValidCellCoordinate(this))
			return nearestCellCoordinateOf(this).getAdjacentCellCoordinate(dir);
		else
		{
			Coordinate res = getCornorCoordinateOfHexagon(dir);
			switch(dir)
			{
				case 0:
					res.x -= 6;
					res.y -= 16;
					break;
				case 1:
					res.x += 6;
					res.y -= 12;
					break;
				case 2:
					res.x += 6;
					break;
				case 3:
					res.x -= 6;
					res.y += 4;
					break;
				case 4:
					res.x -= 16;
					break;
				case 5:
					res.x -= 16;
					res.y -= 12;
					break;
				default:
					System.out.println("getAdjacentCellCoordinate recieves an invalid paramter: dir = " + dir);
					return null;
			}
			if(isValidCellCoordinate(res))
				return res;
			return null;
		}
	};
	public static boolean isUpperRowCellCoordinate(Coordinate c)
	{
		return c.x >= 36 && c.y >= 36 && (c.x % 36 == 0) && (c.y - 36) % 16 == 0;
	}
	
	public static boolean isLowerRowCellCoordinate(Coordinate c)
	{
		return isValidCellCoordinate(c) && ! isUpperRowCellCoordinate(c);
	}
	//TODO: this needs to be tested
	public static Coordinate nearestCellCoordinateOf(Coordinate c)
	{
		//reduce x coordinate to the nearest valid x boundary
		int cx = c.x % 36;
		// focus on the x coordinate:
		// 36 pixels 
		if(cx >= 12 && cx <= 24)
		{
			//c is closer to the upper layer, find the x first
			int qx = c.x;
			if(cx >= 12) 
				qx -= cx;
			else 
				qx += cx;
			
			// now deal with y
			int qy = c.y,
				cy = (c.y - 44) % 16;
			if(cy < 8)
				qy -= cy;
			else
				qy += cy;
			
			return new Coordinate(qx, qy);
		}else
		{
			//c is closer to the lower layer
			//deal with x first
			int qx = c.x;
			if(cx < 12)
				qx -= cx;
			else
				qx += cx;
			//deal with y 
			int qy = c.y,
				cy = (c.y - 36) % 16;
			if(cy < 8)
				qy -= cy;
			else
				qy += cy;
			
			return new Coordinate(qx,qy);
		}
	};
}
