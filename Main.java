package Main;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main
{
	static Lecture [] getDatabase(String type, String userMajor, int userGrade, int userSemester, int userCredit)
	{
		int i = 0;
		Lecture [] lec = null;
		Connection conn = null;
		
		try
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
        	
			String url = "jdbc:mysql://localhost/test?characterEncoding=UTF-8&serverTimezone=UTC";
			//jdbc:mysql://~~~/����̸�, ����ǥ���ʹ� Ÿ���� ���� ������ ����. ������ ū�ϳ�.
        	
			conn = DriverManager.getConnection(url, "root", "1q2w3e4r!");
			//(, "�̸�", "��й�ȣ")
			System.out.println("<���� ����>\n");
        	
			// ���� �ʼ� �ҷ�����
			
			String query = "";
			if(type == "�����ʼ�")
			{
				query = "select * from " + userMajor + "_" + userGrade + "�г�_" + userSemester + "�б�_�����ʼ�";
			}
			else if(type == "��������")
			{
				query = "select * from " + userMajor + "_" + userGrade + "�г�_" + userSemester + "�б�_��������";
			}
			else if(type == "��������")
			{
				query = "select * from " + "����_" + "1�г�_����";
			}
			
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);

			// ���̺� ���� ����
			int tableSize = 0;
			while(rs.next())
			{
				tableSize++;
			}
			
			// ���� �ʼ� ��� ��ü ����
			lec = new Lecture[tableSize];	// ���� �ʼ� ���� DB
			
			rs = st.executeQuery(query);
			while(rs.next())
			{
				int id = rs.getInt("id");
				int grade = rs.getInt("grade");
				String name = rs.getString("classname");
				int credit = rs.getInt("score");
				int number_of_people = rs.getInt("personnumber");
				String professor = rs.getString("professor");
				String building = rs.getString("place");
				int classroom_1 = rs.getInt("cn1");
				int start_1 = rs.getInt("start1");
				int end_1 = rs.getInt("end1");
				int classroom_2 = rs.getInt("cn2");
				int start_2 = rs.getInt("start2");
				int end_2 = rs.getInt("end2");
				
				lec[i++] = new Lecture(name, credit, number_of_people, professor, building,
					classroom_1, start_1, end_1, classroom_2, start_2, end_2);
			}
        	
        	st.close();
        }
        catch(Exception e)
        {
        	System.err.println("�ӻ��ϳ�");
			System.err.println(e.getMessage());
		}
		
		return lec;
	}
	
	// ��� �Լ��� ���� �˰����� ���� ��� �ð�ǥ�� ����� �� ����
	// n = �迭�� ����, r = ������ ����
	static void combination(Lecture[] lec, Schedule [] sche, boolean[] visited, int start, int n, int r, int classes)
    {
        if (r == 0)
        {
            addLecture(lec, sche, visited, n, classes);
            return;
        }

        for (int i = start; i < n; i++)
        {
            visited[i] = true;
            combination(lec, sche, visited, i + 1, n, r - 1, classes);
            visited[i] = false;
        }
    }

    // ���� �߰�
    static void addLecture(Lecture[] lec, Schedule [] sche, boolean[] visited, int n, int classes)
    {
    	int j = 0;
    	
    	for (int i = 0; i < n; i++)
        {
        	for(int k = 0; k < sche[Schedule.num].size; k++)
        	{
        		// �ð�ǥ �ȿ� �̸��� ���� ����(== �й��� �ٸ� ���� ����)�� ���� ��� �ð�ǥ�� �ۼ����� ����
        		if(sche[Schedule.num].lectures[k].name == lec[i].name)
				{
					visited[i] = false;
				}
        		else	// �ð�ǥ�� �̹� �߰��� ���ǿ� ���� �ð��� ��ġ�� �ʴ��� ���� �Ǵ�
        		{
        			int x = 0;
        			int y = 0;
        			
        			int[] term_1 = new int[6];	// �ð�ǥ�� �̹� �߰��� ������ ���� �ð�
					int[] term_2 = new int[6];	// �ð�ǥ�� �߰� ���� ���θ� �Ǵ��ϴ� ������ ���� �ð�
					
					for(x = sche[Schedule.num].lectures[k].start_1; x <= sche[Schedule.num].lectures[k].end_1; x++)
					{
						term_1[y] = x;
						y++;
					}
					
					// classroom_2 == 0�� ��� �� �ֿ� �� ��° ���ǰ� ����
					if(sche[Schedule.num].lectures[k].classroom_2 != 0)
					{
						for(x = sche[Schedule.num].lectures[k].start_2; x <= sche[Schedule.num].lectures[k].end_2; x++)
						{
							term_1[y] = x;
							y++;
						}
					}
					
					y = 0;
					for(x = lec[i].start_1; x <= lec[i].end_1; x++)
					{
						term_2[y] = x;
						y++;
					}
					
					if(lec[i].classroom_2 != 0)
					{
						for(x = lec[i].start_2; x <= lec[i].end_2; x++)
						{
							term_2[y] = x;
							y++;
						}
					}
					
					for(x = 0; x < term_1.length; x++)
					{
						for(y = 0; y < term_2.length; y++)
						{
							// term_1[x] == 0�� ��� �ش� ���ǰ� 1�ð�, �Ǵ� 2�ð� ����(== 3�ð� ������ �ƴ�)�̱� ������ �� �ð��� ��ġ���� Ȯ���� �ʿ䰡 ����
							if(term_1[x] == term_2[y] && term_1[x] != 0 && term_2[y] != 0)
							{
								visited[i] = false;
							}
						}
					}
        		}
			}
        		
        	if (visited[i])
            {
            	sche[Schedule.num].lectures[j].name = lec[i].name;
            	sche[Schedule.num].lectures[j].professor = lec[i].professor;
            	sche[Schedule.num].lectures[j].classroom_1 = lec[i].classroom_1;
            	sche[Schedule.num].lectures[j].start_1 = lec[i].start_1;
            	sche[Schedule.num].lectures[j].end_1 = lec[i].end_1;
            	sche[Schedule.num].lectures[j].classroom_2 = lec[i].classroom_2;
            	sche[Schedule.num].lectures[j].start_2 = lec[i].start_2;
            	sche[Schedule.num].lectures[j].end_2 = lec[i].end_2;
            	sche[Schedule.num].size++;
            	j++;
            }
        }
    	
    	// �ϼ��� �ð�ǥ�� ���� ������ �Էµ� ���� ������ ���ٸ� �ð�ǥ�� possibility ����� true ����
    	if(sche[Schedule.num].size == classes)
    	{
    		sche[Schedule.num].possibility = true;
    	}
    	
    	// �ð�ǥ ���� ����
        Schedule.num++;
    }
	
	public static void main(String [] args)
	{
		
		int i = 0, j = 0;
		int DB_Size = 0;			// ���� DB�� ũ��
		int classes = 0;			// �ð�ǥ�� �߰��� ���� ����
		String classCheck = "";		// ���� �̸��� ���� �й� ���θ� Ȯ���� ����
		
		String userMajor = "";		// ������� �а�
		int userGrade = 0;			// ������� �г�
		int userSemester = 0;		// ������� �б�
		int userCredit = 0;			// ����ڰ� �̼��ϰ� �ϴ� ����
		
		// ����� ���� �Է�
		Scanner scanner= new Scanner(System.in);
		System.out.print("������� �а� : ");
		userMajor = scanner.next();
		System.out.print("������� �г� : ");
		userGrade = scanner.nextInt();
		System.out.print("������� �б� : ");
		userSemester = scanner.nextInt();
		System.out.print("����ڰ� �̼��ϰ��� �ϴ� ���� : ");
		userCredit = scanner.nextInt();
		
		Lecture [] requiredLec;		// ���� �ʼ� ���� DB
		Lecture [] selectionLec;	// ���� ���� ���� DB
		Lecture [] liberal; 		// ���� ���� DB
		requiredLec = getDatabase("�����ʼ�", userMajor, userGrade, userSemester, userCredit);
		selectionLec = getDatabase("��������", userMajor, userGrade, userSemester, userCredit);
		liberal = getDatabase("��������", userMajor, userGrade, userSemester, userCredit);
		
		// ��ǻ�Ͱ���
		
		// �ۼ��� ��� �ð�ǥ�� ������ �迭
		Schedule [] schedules;
		schedules = new Schedule[1000000];
		
		// �ð�ǥ ���� �迭�� ��ü ����
		for(i = 0; i < schedules.length; i++)
		{
			schedules[i] = new Schedule();
		}
		
		// ���� �ʼ� DB �迭 + ���� ���� DB �迭  + ���� DB �迭
		Lecture [] userLec;
		userLec = new Lecture[requiredLec.length + selectionLec.length + liberal.length];
		System.arraycopy(requiredLec, 0, userLec, 0, requiredLec.length);  
		System.arraycopy(selectionLec, 0, userLec, requiredLec.length, selectionLec.length);
		System.arraycopy(liberal, 0, userLec, requiredLec.length + selectionLec.length, liberal.length);
        
		// ���� �̸��� ���� �ð�ǥ�� �߰��� ���� ���� ����
		classCheck = "";
		for(i = 0; i < userLec.length; i++)
		{
			if(!userLec[i].name.equals(classCheck))	// ���� �̸�(classCheck)�� ���� �й� ���� �˻�
			{
				classes++;							// ���� ����  ����
				classCheck = userLec[i].name;		// ���� �̸�(classCheck) ������Ʈ
			}
		}
		
		DB_Size = userLec.length;
		boolean[] visited = new boolean[DB_Size];	// ����� ���� ��� ���� ��� �Լ��� ����� �ο� �迭
		combination(userLec, schedules, visited, 0, DB_Size, 6, 6);
		//combination(userLec, schedules, visited, 0, DB_Size, classes, classes);
        
		// ��� ���
		for(i = 0; i < schedules.length; i++)
		{
			if(schedules[i].possibility)			// �ϼ��� �ð�ǥ�� ���
			{
				System.out.println("�ð�ǥ " + i);
				for(j = 0; j < 10; j++)
				{
					System.out.print(schedules[i].lectures[j].name + " " + schedules[i].lectures[j].professor + " " + schedules[i].lectures[j].classroom_1 + " ");
					System.out.print(schedules[i].lectures[j].start_1 + " " + schedules[i].lectures[j].end_1 + " " + schedules[i].lectures[j].classroom_2 + " ");
					System.out.println(schedules[i].lectures[j].start_2 + " " + schedules[i].lectures[j].end_2);
				}
				System.out.println("");
			}
		}
	}
}
