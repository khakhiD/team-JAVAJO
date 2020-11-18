package Main;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main
{
	static Lecture [] getDatabase(String type, String userMajor, int userGrade, int userSemester)
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
			System.out.println("DB ���� ����");
        	
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
        	System.err.println("DB ���� ����");
			System.err.println(e.getMessage());
		}
		
		return lec;
	}
	
	// ��� �Լ��� ���� �˰����� ���� ��� �ð�ǥ�� ����� �� ����
	// n = �迭�� ����, r = ������ ����
	static void combination(Lecture[] lec, Schedule [] sche, boolean[] visited, int start, int n, int r, int combinationSize, int userCredit, String [] requiredName)
    {
        if (r == 0)
        {
            addLecture(lec, sche, visited, n, combinationSize, userCredit, requiredName);
            return;
        }

        for (int i = start; i < n; i++)
        {
            visited[i] = true;
            combination(lec, sche, visited, i + 1, n, r - 1, combinationSize, userCredit, requiredName);
            visited[i] = false;
        }
    }

    // ���� �߰�
    static void addLecture(Lecture[] lec, Schedule [] sche, boolean[] visited, int n, int combinationSize, int userCredit, String [] requiredName)
    {
    	int i = 0, j = 0, k = 0;
    	
    	for (i = 0; i < n; i++)
        {
        	for(k = 0; k < sche[Schedule.num].size; k++)
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
						term_1[y++] = x;
					}
					
					// classroom_2 == 0�� ��� �� �ֿ� �� ��° ���ǰ� ����
					if(sche[Schedule.num].lectures[k].classroom_2 != 0)
					{
						for(x = sche[Schedule.num].lectures[k].start_2; x <= sche[Schedule.num].lectures[k].end_2; x++)
						{
							term_1[y++] = x;
						}
					}
					
					y = 0;
					for(x = lec[i].start_1; x <= lec[i].end_1; x++)
					{
						term_2[y++] = x;
					}
					
					if(lec[i].classroom_2 != 0)
					{
						for(x = lec[i].start_2; x <= lec[i].end_2; x++)
						{
							term_2[y++] = x;
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
            	sche[Schedule.num].lectures[j] = lec[i];
            	sche[Schedule.num].size++;
            	j++;
            }
        }
    	
    	// �ϼ��� �ð�ǥ�� ���� ������ �Էµ� ���� ������ ���ٸ� �ð�ǥ�� possibility ����� true ����
    	
    	int creditSum = 0;				// �ð�ǥ�� ����� ������ ������ ��
		boolean requiredCheck1 = true;	// �ʼ� ���ǰ� ���� ���ԵǾ����� üũ
		boolean requiredCheck2 = false;
		
		for(i = 0; i < sche[Schedule.num].size; i++)
    	{
    		creditSum += sche[Schedule.num].lectures[i].credit;
    	}
    	
    	for(i = 0; i < sche[Schedule.num].lectures.length; i++)
		{
			if(requiredCheck1 == false)
			{
				break;
			}
				
			for(j = 0; j < requiredName.length; j++)
			{
				if(sche[Schedule.num].lectures[i].name.equals(requiredName[j]))
				{
					requiredCheck2 = true;
				}
			}
			
			if(requiredCheck2 == false)
			{
				requiredCheck1 = false;
			}
		}
    	
    	if(creditSum == userCredit && requiredCheck1 && sche[Schedule.num].size == combinationSize)
    	{
    		sche[Schedule.num].possibility = true;
    	}
    	
    	// �ð�ǥ ���� ����
        Schedule.num++;
    }
	
	public static void main(String [] args)
	{
		int i = 0, j = 0, k = 0;
		
		String userMajor = "��ǻ�Ͱ���";	// ������� �а�
		int userGrade = 2;				// ������� �г�
		int userSemester = 2;			// ������� �б�
		int userCredit = 17;			// ����ڰ� �̼��ϰ� �ϴ� ����
		int userLecSize = 0;			// �й��� ������, ����ڰ� ��û�ϰ��� �ϴ� ���� ����� ����
		
		String classCheck = "";			// ���� �̸��� ���� �й� ���θ� Ȯ���� ����
		String [] requiredName;			// �ʼ� ������ �̸�
		int requiredCount = 0;			// �ʼ� ���� ����
		
		System.out.println("����ڰ� " + userMajor + "�� " + userGrade + "�г� " + userSemester + "�б��̸�, �� �б⿡ " + userCredit + "���� �̼��� ���Ѵٰ� ����");
		System.out.println("����ڰ� ������ ���ǰ� ���־����α׷���, �ڷᱸ��, ��ǻ�ͱ���, ������׽ǽ�, �ڹ����α׷���, �����ں���������, ���ʼ����̶� ����");
		System.out.println("");
		
		Lecture [] requiredLec;		// ���� �ʼ� ���� DB
		Lecture [] selectionLec;	// ���� ���� ���� DB
		Lecture [] liberal; 		// ���� ���� DB
		requiredLec = getDatabase("�����ʼ�", userMajor, userGrade, userSemester);
		selectionLec = getDatabase("��������", userMajor, userGrade, userSemester);
		liberal = getDatabase("��������", userMajor, userGrade, userSemester);
		
		// ���� �˰����� ���� �ۼ��� ��� �ð�ǥ�� ������ �迭
		Schedule [] all_schedules;
		all_schedules = new Schedule[100000];
		// ��� ������ ������Ű�� �ð�ǥ�� ������ �迭
		Schedule [] complete_schedules;
		complete_schedules = new Schedule[1000];
		
		// �ð�ǥ ���� �迭�� ��ü ����
		for(i = 0; i < complete_schedules.length; i++)
		{
			complete_schedules[i] = new Schedule();
		}
		
		// �ÿ��� ����� �Է°� ����
		Lecture [] userLec;
		userLec = new Lecture[16];
		userLec[0] = new Lecture("���־����α׷���", 3, 35, "�ȱ���", "����", 814, 29, 30, 810, 37, 38);
		userLec[1] = new Lecture("���־����α׷���", 3, 35, "�ȱ���", "����", 816, 25, 26, 810, 37, 38);
		userLec[2] = new Lecture("���־����α׷���", 3, 35, "�ֺ���", "����", 814, 3, 3, 814, 17, 18);
		userLec[3] = new Lecture("�ڷᱸ��", 3, 50, "������", "����", 811, 5, 6, 812, 19, 19);
		userLec[4] = new Lecture("�ڷᱸ��", 3, 50, "������", "����", 812, 11, 12, 811, 27, 27);
		userLec[5] = new Lecture("��ǻ�ͱ���", 3, 50, "�̹���", "����", 811, 17, 17, 812, 34, 35);
		userLec[6] = new Lecture("��ǻ�ͱ���", 3, 50, "�̹���", "����", 812, 14, 15, 812, 27, 27);
		userLec[7] = new Lecture("������׽ǽ�", 3, 50, "�̹���", "����", 812, 21, 22, 815, 29, 30);
		userLec[8] = new Lecture("������׽ǽ�", 3, 50, "�̹���", "����", 812, 21, 22, 815, 31, 32);
		userLec[9] = new Lecture("������׽ǽ�", 3, 50, "��ȭ��", "����", 815, 2, 3, 815, 14, 15);
		userLec[10] = new Lecture("�ڹ����α׷���", 3, 33, "��ÿ�", "����", 811, 9, 10, 816, 29, 30);
		userLec[11] = new Lecture("�ڹ����α׷���", 3, 33, "��ÿ�", "����", 811, 9, 10, 817, 31, 32);
		userLec[12] = new Lecture("�ڹ����α׷���", 3, 33, "��ÿ�", "����", 817, 23, 24, 817, 34, 35);
		userLec[13] = new Lecture("�����ں���������", 2, 24, "������", "���", 512, 7, 8, 0, 0, 0);
		userLec[14] = new Lecture("���а��λ�", 2, 20, "�赵��", "1��", 504, 14, 15, 0, 0, 0);
		userLec[15] = new Lecture("���ʼ���", 3, 46, "����ĥ", "����", 615, 29, 30, 615, 33, 33);
		
		classCheck = "";
		for(i = 0; i < requiredLec.length; i++)
		{
			if(!requiredLec[i].name.equals(classCheck))	// ���� �̸�(classCheck)�� ���� �й� ���� �˻�
			{
				requiredCount++;						// ���� ����  ����
				classCheck = requiredLec[i].name;		// ���� �̸�(classCheck) ������Ʈ
			}
		}
		
		requiredName = new String[requiredCount];
		
		classCheck = "";
		j = 0;
		for(i = 0; i < requiredLec.length; i++)
		{
			if(!requiredLec[i].name.equals(classCheck))
			{
				requiredName[j++] = requiredLec[i].name;
				classCheck = requiredLec[i].name;
			}
		}
		
		userLecSize = userLec.length;
		boolean[] visited = null;
		
		for(int l = 1; l < 11; l++)
		{
			// �ð�ǥ ���� �迭�� ��ü ����
			for(i = 0; i < all_schedules.length; i++)
			{
				all_schedules[i] = new Schedule();
			}
			
			visited = new boolean[userLecSize];	// ����� ���� ��� ���� ��� �Լ��� ����� �ο� �迭
			combination(userLec, all_schedules, visited, 0, userLecSize, l, l, userCredit, requiredName);
			
			// ��� ������ ������Ű�� �ð�ǥ�� ����
			for(i = 0; i < all_schedules.length; i++)
			{
				if(all_schedules[i].possibility)
				{	
					complete_schedules[k++].lectures = all_schedules[i].lectures;
				}
			}
		}
		
		System.out.println("");
		for(i = 0; i < k; i++)
		{
			System.out.println("�ð�ǥ " + (i + 1));
			for(j = 0; j < complete_schedules[0].lectures.length; j++)
			{
				if(complete_schedules[i].lectures[j].name != "")
				{
					System.out.print(complete_schedules[i].lectures[j].name + " " + complete_schedules[i].lectures[j].professor + " " + complete_schedules[i].lectures[j].classroom_1 + " ");
					System.out.print(complete_schedules[i].lectures[j].start_1 + " " + complete_schedules[i].lectures[j].end_1 + " " + complete_schedules[i].lectures[j].classroom_2 + " ");
					System.out.println(complete_schedules[i].lectures[j].start_2 + " " + complete_schedules[i].lectures[j].end_2);
				}
			}
			System.out.println("");
		}
		
		System.out.println("���α׷� ����");
	}
}
