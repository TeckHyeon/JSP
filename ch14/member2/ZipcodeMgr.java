package member2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import ch14.DBConnectionMgr;

public class ZipcodeMgr {

	private DBConnectionMgr pool;
	
	public ZipcodeMgr() {
		pool = DBConnectionMgr.getInstance();
	}

	public Vector<ZipcodeBean> zipcodeRead(String area3) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = null;
		ResultSet rs = null;
		Vector<ZipcodeBean> vlist = new Vector<ZipcodeBean>();
		try {
			con = pool.getConnection();
			sql = "SELECT * FROM tblZipcode where area3 like ? ";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, "%"+area3+"%");
			rs = pstmt.executeQuery();

			while (rs.next()) {
				ZipcodeBean bean = new ZipcodeBean();
				bean.setZipcode(rs.getString(1));
				bean.setArea1(rs.getString(2));
				bean.setArea2(rs.getString(3));
				bean.setArea3(rs.getString(4).trim());		
				vlist.addElement(bean);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}
}
