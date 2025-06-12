package org.viettel.vgov.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.viettel.vgov.model.*;
import org.viettel.vgov.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final WorkLogRepository workLogRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            logger.info("Initializing sample data for V-GOV system...");
            
            // Initialize in correct order to respect foreign key constraints
            List<User> users = initializeUsers();
            List<Project> projects = initializeProjects(users);
            initializeProjectMembers(users, projects);
            initializeWorkLogs(users, projects);
            initializeNotifications(users, projects);
            
            logger.info("Sample data initialization completed successfully!");
            logLoginCredentials();
        } else {
            logger.info("Database already contains data, skipping initialization");
        }
    }
    
    private List<User> initializeUsers() {
        logger.info("Creating sample users...");
        List<User> users = new ArrayList<>();
        
        // Admin user
        User admin = createUser("ADMIN001", "Quản Trị Viên Hệ Thống", "admin@vgov.vn", "123456",
                                User.Role.admin, User.Gender.other, LocalDate.of(1980, 1, 1));
        users.add(userRepository.save(admin));
        
        // Project Managers
        User pm1 = createUser("PM001", "Nguyễn Văn Quản", "pm1@vgov.vn", "123456",
                              User.Role.pm, User.Gender.male, LocalDate.of(1985, 5, 15));
        pm1.setCreatedBy(admin);
        users.add(userRepository.save(pm1));
        
        User pm2 = createUser("PM002", "Trần Thị Linh", "pm2@vgov.vn", "123456",
                              User.Role.pm, User.Gender.female, LocalDate.of(1987, 8, 22));
        pm2.setCreatedBy(admin);
        users.add(userRepository.save(pm2));
        
        User pm3 = createUser("PM003", "Lý Văn Hùng", "pm3@vgov.vn", "123456",
                              User.Role.pm, User.Gender.male, LocalDate.of(1986, 3, 8));
        pm3.setCreatedBy(admin);
        users.add(userRepository.save(pm3));
        
        // Senior Developers
        User dev1 = createUser("DEV001", "Lê Văn Đức", "dev1@vgov.vn", "123456",
                               User.Role.dev, User.Gender.male, LocalDate.of(1990, 3, 10));
        dev1.setCreatedBy(admin);
        users.add(userRepository.save(dev1));
        
        User dev2 = createUser("DEV002", "Phạm Thị Mai", "dev2@vgov.vn", "123456",
                               User.Role.dev, User.Gender.female, LocalDate.of(1992, 7, 18));
        dev2.setCreatedBy(admin);
        users.add(userRepository.save(dev2));
        
        User dev3 = createUser("DEV003", "Hồ Văn Tuấn", "dev3@vgov.vn", "123456",
                               User.Role.dev, User.Gender.male, LocalDate.of(1991, 11, 5));
        dev3.setCreatedBy(admin);
        users.add(userRepository.save(dev3));
        
        User dev4 = createUser("DEV004", "Võ Thị Hương", "dev4@vgov.vn", "123456",
                               User.Role.dev, User.Gender.female, LocalDate.of(1993, 2, 28));
        dev4.setCreatedBy(admin);
        users.add(userRepository.save(dev4));
        
        User dev5 = createUser("DEV005", "Trương Văn Nam", "dev5@vgov.vn", "123456",
                               User.Role.dev, User.Gender.male, LocalDate.of(1989, 12, 20));
        dev5.setCreatedBy(admin);
        users.add(userRepository.save(dev5));
        
        User dev6 = createUser("DEV006", "Nguyễn Thị Thảo", "dev6@vgov.vn", "123456",
                               User.Role.dev, User.Gender.female, LocalDate.of(1994, 6, 15));
        dev6.setCreatedBy(admin);
        users.add(userRepository.save(dev6));
        
        User dev7 = createUser("DEV007", "Phan Văn Khoa", "dev7@vgov.vn", "123456",
                               User.Role.dev, User.Gender.male, LocalDate.of(1992, 9, 3));
        dev7.setCreatedBy(admin);
        users.add(userRepository.save(dev7));
        
        User dev8 = createUser("DEV008", "Lê Thị Yến", "dev8@vgov.vn", "123456",
                               User.Role.dev, User.Gender.female, LocalDate.of(1993, 4, 25));
        dev8.setCreatedBy(admin);
        users.add(userRepository.save(dev8));
        
        // Business Analysts
        User ba1 = createUser("BA001", "Đặng Văn Minh", "ba1@vgov.vn", "123456",
                              User.Role.ba, User.Gender.male, LocalDate.of(1988, 9, 12));
        ba1.setCreatedBy(admin);
        users.add(userRepository.save(ba1));
        
        User ba2 = createUser("BA002", "Bùi Thị Lan", "ba2@vgov.vn", "123456",
                              User.Role.ba, User.Gender.female, LocalDate.of(1989, 4, 20));
        ba2.setCreatedBy(admin);
        users.add(userRepository.save(ba2));
        
        User ba3 = createUser("BA003", "Hoàng Văn Sơn", "ba3@vgov.vn", "123456",
                              User.Role.ba, User.Gender.male, LocalDate.of(1987, 11, 8));
        ba3.setCreatedBy(admin);
        users.add(userRepository.save(ba3));
        
        User ba4 = createUser("BA004", "Vũ Thị Hoa", "ba4@vgov.vn", "123456",
                              User.Role.ba, User.Gender.female, LocalDate.of(1990, 1, 30));
        ba4.setCreatedBy(admin);
        users.add(userRepository.save(ba4));
        
        // Testers
        User test1 = createUser("TEST001", "Ngô Văn Thắng", "test1@vgov.vn", "123456",
                                User.Role.test, User.Gender.male, LocalDate.of(1990, 6, 14));
        test1.setCreatedBy(admin);
        users.add(userRepository.save(test1));
        
        User test2 = createUser("TEST002", "Dương Thị Nga", "test2@vgov.vn", "123456",
                                User.Role.test, User.Gender.female, LocalDate.of(1991, 12, 3));
        test2.setCreatedBy(admin);
        users.add(userRepository.save(test2));
        
        User test3 = createUser("TEST003", "Cao Văn Đạt", "test3@vgov.vn", "123456",
                                User.Role.test, User.Gender.male, LocalDate.of(1992, 8, 17));
        test3.setCreatedBy(admin);
        users.add(userRepository.save(test3));
        
        User test4 = createUser("TEST004", "Đinh Thị Kim", "test4@vgov.vn", "123456",
                                User.Role.test, User.Gender.female, LocalDate.of(1993, 5, 12));
        test4.setCreatedBy(admin);
        users.add(userRepository.save(test4));
        
        logger.info("Created {} sample users", users.size());
        return users;
    }
    
    private User createUser(String employeeCode, String fullName, String email, String password,
                           User.Role role, User.Gender gender, LocalDate birthDate) {
        User user = new User();
        user.setEmployeeCode(employeeCode);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        user.setGender(gender);
        user.setBirthDate(birthDate);
        user.setIsActive(true);
        return user;
    }
    
    private List<Project> initializeProjects(List<User> users) {
        logger.info("Creating sample projects...");
        List<Project> projects = new ArrayList<>();
        
        User admin = users.get(0);
        User pm1 = users.get(1); // pm1@vgov.vn
        User pm2 = users.get(2); // pm2@vgov.vn
        
        // 2023 Projects (Completed) - Q1 2023
        Project proj01 = createProject("PROJ001", "Hệ Thống CRM Doanh Nghiệp", pm1.getEmail(),
                                       LocalDate.of(2023, 1, 15), LocalDate.of(2023, 4, 30),
                                       Project.ProjectType.TM, Project.Status.Closed,
                                       "Phát triển hệ thống quản lý khách hàng cho công ty bảo hiểm Singapore.", admin);
        projects.add(projectRepository.save(proj01));
        
        Project proj02 = createProject("PROJ002", "Ứng Dụng Mobile E-Wallet", pm2.getEmail(),
                                       LocalDate.of(2023, 2, 1), LocalDate.of(2023, 5, 15),
                                       Project.ProjectType.Package, Project.Status.Closed,
                                       "Xây dựng ví điện tử cho thị trường Việt Nam với tích hợp ngân hàng.", admin);
        projects.add(projectRepository.save(proj02));
        
        Project proj03 = createProject("PROJ003", "Hệ Thống ERP Manufacturing", pm1.getEmail(),
                                       LocalDate.of(2023, 3, 1), LocalDate.of(2023, 6, 30),
                                       Project.ProjectType.OSDC, Project.Status.Closed,
                                       "Phát triển ERP cho nhà máy sản xuất ô tô tại Thái Lan.", admin);
        projects.add(projectRepository.save(proj03));
        
        // Q2 2023
        Project proj04 = createProject("PROJ004", "Portal Giáo Dục Trực Tuyến", pm2.getEmail(),
                                       LocalDate.of(2023, 4, 15), LocalDate.of(2023, 7, 31),
                                       Project.ProjectType.TM, Project.Status.Closed,
                                       "Nền tảng học tập online cho trường đại học Malaysia.", admin);
        projects.add(projectRepository.save(proj04));
        
        Project proj05 = createProject("PROJ005", "Hệ Thống Logistics AI", pm1.getEmail(),
                                       LocalDate.of(2023, 5, 1), LocalDate.of(2023, 8, 15),
                                       Project.ProjectType.Package, Project.Status.Closed,
                                       "Tối ưu hóa vận chuyển bằng AI cho công ty logistics Hàn Quốc.", admin);
        projects.add(projectRepository.save(proj05));
        
        Project proj06 = createProject("PROJ006", "Platform IoT Smart City", pm2.getEmail(),
                                       LocalDate.of(2023, 6, 1), LocalDate.of(2023, 9, 30),
                                       Project.ProjectType.OSDC, Project.Status.Closed,
                                       "Hệ thống IoT cho thành phố thông minh tại Indonesia.", admin);
        projects.add(projectRepository.save(proj06));
        
        // Q3 2023
        Project proj07 = createProject("PROJ007", "Banking Core System", pm1.getEmail(),
                                       LocalDate.of(2023, 7, 15), LocalDate.of(2023, 10, 31),
                                       Project.ProjectType.Package, Project.Status.Closed,
                                       "Hệ thống ngân hàng lõi cho ngân hàng digital Philippines.", admin);
        projects.add(projectRepository.save(proj07));
        
        Project proj08 = createProject("PROJ008", "E-Commerce Platform", pm2.getEmail(),
                                       LocalDate.of(2023, 8, 1), LocalDate.of(2023, 11, 15),
                                       Project.ProjectType.TM, Project.Status.Closed,
                                       "Nền tảng thương mại điện tử B2B cho thị trường Úc.", admin);
        projects.add(projectRepository.save(proj08));
        
        Project proj09 = createProject("PROJ009", "Healthcare Management", pm1.getEmail(),
                                       LocalDate.of(2023, 9, 1), LocalDate.of(2023, 12, 31),
                                       Project.ProjectType.OSDC, Project.Status.Closed,
                                       "Hệ thống quản lý bệnh viện cho Nhật Bản.", admin);
        projects.add(projectRepository.save(proj09));
        
        // Q4 2023
        Project proj10 = createProject("PROJ010", "Fintech Mobile App", pm2.getEmail(),
                                       LocalDate.of(2023, 10, 15), LocalDate.of(2024, 1, 31),
                                       Project.ProjectType.TM, Project.Status.Closed,
                                       "Ứng dụng fintech cho startup Singapore.", admin);
        projects.add(projectRepository.save(proj10));
        
        Project proj11 = createProject("PROJ011", "Supply Chain Analytics", pm1.getEmail(),
                                       LocalDate.of(2023, 11, 1), LocalDate.of(2024, 2, 15),
                                       Project.ProjectType.Package, Project.Status.Closed,
                                       "Phân tích chuỗi cung ứng bằng AI cho tập đoàn Hàn Quốc.", admin);
        projects.add(projectRepository.save(proj11));
        
        Project proj12 = createProject("PROJ012", "Digital Transformation Portal", pm2.getEmail(),
                                       LocalDate.of(2023, 12, 1), LocalDate.of(2024, 3, 31),
                                       Project.ProjectType.OSDC, Project.Status.Closed,
                                       "Chuyển đổi số cho chính phủ Malaysia.", admin);
        projects.add(projectRepository.save(proj12));
        
        // 2024 Projects - Q1 2024
        Project proj13 = createProject("PROJ013", "AI Customer Service", pm1.getEmail(),
                                       LocalDate.of(2024, 1, 15), LocalDate.of(2024, 4, 30),
                                       Project.ProjectType.TM, Project.Status.Closed,
                                       "Hệ thống chăm sóc khách hàng AI cho ngân hàng Thái Lan.", admin);
        projects.add(projectRepository.save(proj13));
        
        Project proj14 = createProject("PROJ014", "Cloud Migration Platform", pm2.getEmail(),
                                       LocalDate.of(2024, 2, 1), LocalDate.of(2024, 5, 15),
                                       Project.ProjectType.Package, Project.Status.Closed,
                                       "Di chuyển hệ thống lên cloud cho tập đoàn Indonesia.", admin);
        projects.add(projectRepository.save(proj14));
        
        Project proj15 = createProject("PROJ015", "Blockchain Trade Finance", pm1.getEmail(),
                                       LocalDate.of(2024, 3, 15), LocalDate.of(2024, 6, 30),
                                       Project.ProjectType.TM, Project.Status.Closed,
                                       "Tài chính thương mại blockchain cho ngân hàng Singapore.", admin);
        projects.add(projectRepository.save(proj15));
        
        // Q2 2024
        Project proj16 = createProject("PROJ016", "Smart Factory IoT", pm2.getEmail(),
                                       LocalDate.of(2024, 4, 1), LocalDate.of(2024, 7, 31),
                                       Project.ProjectType.OSDC, Project.Status.Closed,
                                       "Nhà máy thông minh IoT cho công ty sản xuất Nhật Bản.", admin);
        projects.add(projectRepository.save(proj16));
        
        Project proj17 = createProject("PROJ017", "VR Training Platform", pm1.getEmail(),
                                       LocalDate.of(2024, 5, 1), LocalDate.of(2024, 8, 15),
                                       Project.ProjectType.TM, Project.Status.Closed,
                                       "Nền tảng đào tạo VR cho ngành hàng không.", admin);
        projects.add(projectRepository.save(proj17));
        
        Project proj18 = createProject("PROJ018", "Quantum Computing Research", pm2.getEmail(),
                                       LocalDate.of(2024, 6, 15), LocalDate.of(2024, 9, 30),
                                       Project.ProjectType.Package, Project.Status.Closed,
                                       "Nghiên cứu ứng dụng quantum computing trong tài chính.", admin);
        projects.add(projectRepository.save(proj18));
        
        // Q3 2024
        Project proj19 = createProject("PROJ019", "Metaverse Retail Experience", pm1.getEmail(),
                                       LocalDate.of(2024, 7, 1), LocalDate.of(2024, 10, 31),
                                       Project.ProjectType.TM, Project.Status.Closed,
                                       "Trải nghiệm bán lẻ trong metaverse.", admin);
        projects.add(projectRepository.save(proj19));
        
        Project proj20 = createProject("PROJ020", "Green Energy Management", pm2.getEmail(),
                                       LocalDate.of(2024, 8, 15), LocalDate.of(2024, 11, 30),
                                       Project.ProjectType.OSDC, Project.Status.Closed,
                                       "Quản lý năng lượng xanh bằng AI và IoT.", admin);
        projects.add(projectRepository.save(proj20));
        
        Project proj21 = createProject("PROJ021", "Cybersecurity Platform", pm1.getEmail(),
                                       LocalDate.of(2024, 9, 1), LocalDate.of(2024, 12, 15),
                                       Project.ProjectType.Package, Project.Status.Closed,
                                       "Nền tảng bảo mật mạng cho doanh nghiệp lớn.", admin);
        projects.add(projectRepository.save(proj21));
        
        // Q4 2024
        Project proj22 = createProject("PROJ022", "Smart Agriculture IoT", pm2.getEmail(),
                                       LocalDate.of(2024, 10, 1), LocalDate.of(2025, 1, 31),
                                       Project.ProjectType.TM, Project.Status.Closed,
                                       "Hệ thống nông nghiệp thông minh cho Việt Nam.", admin);
        projects.add(projectRepository.save(proj22));
        
        Project proj23 = createProject("PROJ023", "Digital Banking Suite", pm1.getEmail(),
                                       LocalDate.of(2024, 11, 15), LocalDate.of(2025, 2, 28),
                                       Project.ProjectType.OSDC, Project.Status.Closed,
                                       "Bộ giải pháp ngân hàng số toàn diện.", admin);
        projects.add(projectRepository.save(proj23));
        
        Project proj24 = createProject("PROJ024", "AI Medical Diagnosis", pm2.getEmail(),
                                       LocalDate.of(2024, 12, 1), LocalDate.of(2025, 3, 31),
                                       Project.ProjectType.Package, Project.Status.Closed,
                                       "Hệ thống chẩn đoán y tế bằng AI.", admin);
        projects.add(projectRepository.save(proj24));
        
        // 2025 Current Active Projects - Q1 2025
        Project proj25 = createProject("PROJ025", "Autonomous Vehicle Control", pm1.getEmail(),
                                       LocalDate.of(2025, 1, 15), LocalDate.of(2025, 7, 31),
                                       Project.ProjectType.TM, Project.Status.InProgress,
                                       "Hệ thống điều khiển xe tự lái cho công ty ô tô Hàn Quốc.", admin);
        projects.add(projectRepository.save(proj25));
        
        Project proj26 = createProject("PROJ026", "Smart City Dashboard", pm2.getEmail(),
                                       LocalDate.of(2025, 2, 1), LocalDate.of(2025, 8, 15),
                                       Project.ProjectType.OSDC, Project.Status.InProgress,
                                       "Bảng điều khiển thành phố thông minh cho Singapore.", admin);
        projects.add(projectRepository.save(proj26));
        
        Project proj27 = createProject("PROJ027", "Blockchain Supply Chain", pm1.getEmail(),
                                       LocalDate.of(2025, 3, 15), LocalDate.of(2025, 9, 30),
                                       Project.ProjectType.Package, Project.Status.InProgress,
                                       "Chuỗi cung ứng blockchain cho ngành dược phẩm.", admin);
        projects.add(projectRepository.save(proj27));
        
        // Q2 2025 - Current Active
        Project proj28 = createProject("PROJ028", "Neural Network Trading", pm2.getEmail(),
                                       LocalDate.of(2025, 4, 1), LocalDate.of(2025, 10, 31),
                                       Project.ProjectType.TM, Project.Status.InProgress,
                                       "Hệ thống giao dịch mạng neural cho quỹ đầu tư.", admin);
        projects.add(projectRepository.save(proj28));
        
        Project proj29 = createProject("PROJ029", "Quantum Encryption Service", pm1.getEmail(),
                                       LocalDate.of(2025, 5, 15), LocalDate.of(2025, 11, 30),
                                       Project.ProjectType.OSDC, Project.Status.InProgress,
                                       "Dịch vụ mã hóa lượng tử cho ngân hàng trung ương.", admin);
        projects.add(projectRepository.save(proj29));
        
        Project proj30 = createProject("PROJ030", "AR Shopping Experience", pm2.getEmail(),
                                       LocalDate.of(2025, 6, 1), LocalDate.of(2025, 12, 15),
                                       Project.ProjectType.Package, Project.Status.InProgress,
                                       "Trải nghiệm mua sắm thực tế ảo cho chuỗi bán lẻ.", admin);
        projects.add(projectRepository.save(proj30));
        
        // On Hold Projects
        Project proj31 = createProject("PROJ031", "Space Communication Network", pm1.getEmail(),
                                       LocalDate.of(2025, 4, 15), LocalDate.of(2026, 2, 28),
                                       Project.ProjectType.TM, Project.Status.Hold,
                                       "Mạng truyền thông vệ tinh cho dự án không gian.", admin);
        projects.add(projectRepository.save(proj31));
        
        // Future Presale Projects
        Project proj32 = createProject("PROJ032", "Holographic Conference System", pm2.getEmail(),
                                       LocalDate.of(2025, 7, 1), null,
                                       Project.ProjectType.Presale, Project.Status.Presale,
                                       "Hệ thống hội nghị hologram cho doanh nghiệp toàn cầu.", admin);
        projects.add(projectRepository.save(proj32));
        
        Project proj33 = createProject("PROJ033", "Brain-Computer Interface", pm1.getEmail(),
                                       LocalDate.of(2025, 8, 15), null,
                                       Project.ProjectType.Presale, Project.Status.Presale,
                                       "Giao diện não-máy tính cho ứng dụng y tế.", admin);
        projects.add(projectRepository.save(proj33));
        
        Project proj34 = createProject("PROJ034", "Fusion Energy Monitor", pm2.getEmail(),
                                       LocalDate.of(2025, 9, 1), null,
                                       Project.ProjectType.Presale, Project.Status.Presale,
                                       "Hệ thống giám sát năng lượng fusion.", admin);
        projects.add(projectRepository.save(proj34));
        
        // Additional Active Projects for Better Workload Distribution
        User pm3 = users.get(3); // pm3@vgov.vn - this is actually PM003
        
        Project proj35 = createProject("PROJ035", "Cloud Native Microservices", pm1.getEmail(),
                                       LocalDate.of(2025, 6, 15), LocalDate.of(2025, 12, 31),
                                       Project.ProjectType.TM, Project.Status.InProgress,
                                       "Chuyển đổi monolith sang microservices cho ngân hàng Malaysia.", admin);
        projects.add(projectRepository.save(proj35));
        
        Project proj36 = createProject("PROJ036", "Digital Identity Platform", pm2.getEmail(),
                                       LocalDate.of(2025, 5, 1), LocalDate.of(2025, 11, 15),
                                       Project.ProjectType.OSDC, Project.Status.InProgress,
                                       "Nền tảng định danh số cho chính phủ Indonesia.", admin);
        projects.add(projectRepository.save(proj36));
        
        Project proj37 = createProject("PROJ037", "Real-time Analytics Engine", pm3.getEmail(),
                                       LocalDate.of(2025, 4, 15), LocalDate.of(2025, 10, 30),
                                       Project.ProjectType.Package, Project.Status.InProgress,
                                       "Engine phân tích dữ liệu real-time cho tập đoàn Hàn Quốc.", admin);
        projects.add(projectRepository.save(proj37));
        
        Project proj38 = createProject("PROJ038", "Mobile Payment Gateway", pm1.getEmail(),
                                       LocalDate.of(2025, 3, 1), LocalDate.of(2025, 9, 15),
                                       Project.ProjectType.TM, Project.Status.InProgress,
                                       "Cổng thanh toán di động cho startup Thái Lan.", admin);
        projects.add(projectRepository.save(proj38));
        
        Project proj39 = createProject("PROJ039", "Supply Chain Visibility", pm2.getEmail(),
                                       LocalDate.of(2025, 5, 15), LocalDate.of(2025, 11, 30),
                                       Project.ProjectType.OSDC, Project.Status.InProgress,
                                       "Hệ thống minh bạch chuỗi cung ứng cho công ty logistics Singapore.", admin);
        projects.add(projectRepository.save(proj39));
        
        Project proj40 = createProject("PROJ040", "AI Content Moderation", pm3.getEmail(),
                                       LocalDate.of(2025, 6, 1), LocalDate.of(2025, 12, 15),
                                       Project.ProjectType.Package, Project.Status.InProgress,
                                       "Hệ thống kiểm duyệt nội dung AI cho mạng xã hội.", admin);
        projects.add(projectRepository.save(proj40));
        
        Project proj41 = createProject("PROJ041", "Green Finance Platform", pm1.getEmail(),
                                       LocalDate.of(2025, 4, 1), LocalDate.of(2025, 10, 15),
                                       Project.ProjectType.TM, Project.Status.InProgress,
                                       "Nền tảng tài chính xanh cho ngân hàng Philippines.", admin);
        projects.add(projectRepository.save(proj41));
        
        Project proj42 = createProject("PROJ042", "Smart Contract Auditor", pm2.getEmail(),
                                       LocalDate.of(2025, 3, 15), LocalDate.of(2025, 9, 30),
                                       Project.ProjectType.OSDC, Project.Status.InProgress,
                                       "Công cụ audit smart contract tự động cho DeFi.", admin);
        projects.add(projectRepository.save(proj42));
        
        Project proj43 = createProject("PROJ043", "Customer Journey Analytics", pm3.getEmail(),
                                       LocalDate.of(2025, 5, 1), LocalDate.of(2025, 11, 15),
                                       Project.ProjectType.Package, Project.Status.InProgress,
                                       "Phân tích hành trình khách hàng cho retail Nhật Bản.", admin);
        projects.add(projectRepository.save(proj43));
        
        Project proj44 = createProject("PROJ044", "Edge Computing Platform", pm1.getEmail(),
                                       LocalDate.of(2025, 6, 15), LocalDate.of(2025, 12, 31),
                                       Project.ProjectType.TM, Project.Status.InProgress,
                                       "Nền tảng edge computing cho IoT manufacturing.", admin);
        projects.add(projectRepository.save(proj44));
        
        // Additional On Hold Projects
        Project proj45 = createProject("PROJ045", "Quantum Machine Learning", pm2.getEmail(),
                                       LocalDate.of(2025, 7, 1), LocalDate.of(2026, 3, 31),
                                       Project.ProjectType.OSDC, Project.Status.Hold,
                                       "Nghiên cứu machine learning trên quantum computer.", admin);
        projects.add(projectRepository.save(proj45));
        
        Project proj46 = createProject("PROJ046", "Neural Interface SDK", pm3.getEmail(),
                                       LocalDate.of(2025, 8, 1), LocalDate.of(2026, 4, 30),
                                       Project.ProjectType.Package, Project.Status.Hold,
                                       "SDK cho giao diện thần kinh cho game VR.", admin);
        projects.add(projectRepository.save(proj46));
        
        logger.info("Created {} sample projects", projects.size());
        return projects;
    }
    
    private Project createProject(String projectCode, String projectName, String pmEmail,
                                 LocalDate startDate, LocalDate endDate, Project.ProjectType projectType,
                                 Project.Status status, String description, User createdBy) {
        Project project = new Project();
        project.setProjectCode(projectCode);
        project.setProjectName(projectName);
        project.setPmEmail(pmEmail);
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        project.setProjectType(projectType);
        project.setStatus(status);
        project.setDescription(description);
        project.setCreatedBy(createdBy);
        return project;
    }
    
    private void initializeProjectMembers(List<User> users, List<Project> projects) {
        logger.info("Creating project member assignments...");
        
        // Filter users by role for assignment
        List<User> pms = users.stream().filter(u -> u.getRole() == User.Role.pm).collect(Collectors.toList());
        List<User> devs = users.stream().filter(u -> u.getRole() == User.Role.dev).collect(Collectors.toList());
        List<User> bas = users.stream().filter(u -> u.getRole() == User.Role.ba).collect(Collectors.toList());
        List<User> testers = users.stream().filter(u -> u.getRole() == User.Role.test).collect(Collectors.toList());
        User admin = users.get(0);
        
        Random random = new Random(42); // Fixed seed for consistent results
        
        // Track current workload for each user to prevent exceeding 100%
        Map<Long, BigDecimal> currentWorkloads = new HashMap<>();
        
        // First pass: Assign to active projects only, then closed projects
        List<Project> activeProjects = projects.stream()
            .filter(p -> p.getStatus() == Project.Status.InProgress || p.getStatus() == Project.Status.Hold)
            .collect(Collectors.toList());
        
        List<Project> closedProjects = projects.stream()
            .filter(p -> p.getStatus() == Project.Status.Closed)
            .collect(Collectors.toList());
        
        // Process active projects first to ensure proper workload distribution
        for (Project project : activeProjects) {
            // Assign PM (rotate through available PMs)
            User pm = pms.get(random.nextInt(pms.size()));
            BigDecimal pmWorkload = getAvailableWorkload(pm.getId(), currentWorkloads, 15, 25); // 15-25%
            
            if (pmWorkload.compareTo(BigDecimal.ZERO) > 0) {
                createProjectMember(project, pm, pmWorkload, admin);
                currentWorkloads.put(pm.getId(), 
                    currentWorkloads.getOrDefault(pm.getId(), BigDecimal.ZERO).add(pmWorkload));
            }
            
            // Assign 2-4 developers per project with reduced workload
            int devCount = 2 + random.nextInt(3); // 2-4 developers
            Set<User> assignedDevs = new HashSet<>();
            for (int j = 0; j < devCount && j < devs.size(); j++) {
                User dev = getAvailableUser(devs, assignedDevs, currentWorkloads, random);
                if (dev != null) {
                    BigDecimal devWorkload = getAvailableWorkload(dev.getId(), currentWorkloads, 25, 45); // 25-45%
                    if (devWorkload.compareTo(BigDecimal.ZERO) > 0) {
                        assignedDevs.add(dev);
                        createProjectMember(project, dev, devWorkload, admin);
                        currentWorkloads.put(dev.getId(), 
                            currentWorkloads.getOrDefault(dev.getId(), BigDecimal.ZERO).add(devWorkload));
                    }
                }
            }
            
            // Assign 1-2 BAs per project with reduced workload
            int baCount = 1 + random.nextInt(2); // 1-2 BAs
            Set<User> assignedBAs = new HashSet<>();
            for (int j = 0; j < baCount && j < bas.size(); j++) {
                User ba = getAvailableUser(bas, assignedBAs, currentWorkloads, random);
                if (ba != null) {
                    BigDecimal baWorkload = getAvailableWorkload(ba.getId(), currentWorkloads, 20, 35); // 20-35%
                    if (baWorkload.compareTo(BigDecimal.ZERO) > 0) {
                        assignedBAs.add(ba);
                        createProjectMember(project, ba, baWorkload, admin);
                        currentWorkloads.put(ba.getId(), 
                            currentWorkloads.getOrDefault(ba.getId(), BigDecimal.ZERO).add(baWorkload));
                    }
                }
            }
            
            // Assign 1-2 testers per project with reduced workload
            int testerCount = 1 + random.nextInt(2); // 1-2 testers
            Set<User> assignedTesters = new HashSet<>();
            for (int j = 0; j < testerCount && j < testers.size(); j++) {
                User tester = getAvailableUser(testers, assignedTesters, currentWorkloads, random);
                if (tester != null) {
                    BigDecimal testerWorkload = getAvailableWorkload(tester.getId(), currentWorkloads, 20, 35); // 20-35%
                    if (testerWorkload.compareTo(BigDecimal.ZERO) > 0) {
                        assignedTesters.add(tester);
                        createProjectMember(project, tester, testerWorkload, admin);
                        currentWorkloads.put(tester.getId(), 
                            currentWorkloads.getOrDefault(tester.getId(), BigDecimal.ZERO).add(testerWorkload));
                    }
                }
            }
        }
        
        // Process closed projects separately (these don't affect current workload)
        // Keep members active for closed projects since they completed the project successfully
        for (Project project : closedProjects) {
            // Assign PM
            User pm = pms.get(random.nextInt(pms.size()));
            BigDecimal pmWorkload = new BigDecimal(15 + random.nextInt(16)); // 15-30%
            
            createProjectMember(project, pm, pmWorkload, admin);
            
            // Assign developers
            int devCount = 2 + random.nextInt(3); // 2-4 developers
            Set<User> assignedDevs = new HashSet<>();
            for (int j = 0; j < devCount && j < devs.size(); j++) {
                User dev;
                int attempts = 0;
                do {
                    dev = devs.get(random.nextInt(devs.size()));
                    attempts++;
                } while (assignedDevs.contains(dev) && attempts < 20);
                
                if (!assignedDevs.contains(dev)) {
                    assignedDevs.add(dev);
                    BigDecimal devWorkload = new BigDecimal(25 + random.nextInt(26)); // 25-50%
                    
                    createProjectMember(project, dev, devWorkload, admin);
                }
            }
            
            // Assign BAs
            int baCount = 1 + random.nextInt(2); // 1-2 BAs
            Set<User> assignedBAs = new HashSet<>();
            for (int j = 0; j < baCount && j < bas.size(); j++) {
                User ba;
                int attempts = 0;
                do {
                    ba = bas.get(random.nextInt(bas.size()));
                    attempts++;
                } while (assignedBAs.contains(ba) && attempts < 20);
                
                if (!assignedBAs.contains(ba)) {
                    assignedBAs.add(ba);
                    BigDecimal baWorkload = new BigDecimal(20 + random.nextInt(21)); // 20-40%
                    
                    createProjectMember(project, ba, baWorkload, admin);
                }
            }
            
            // Assign testers
            int testerCount = 1 + random.nextInt(2); // 1-2 testers
            Set<User> assignedTesters = new HashSet<>();
            for (int j = 0; j < testerCount && j < testers.size(); j++) {
                User tester;
                int attempts = 0;
                do {
                    tester = testers.get(random.nextInt(testers.size()));
                    attempts++;
                } while (assignedTesters.contains(tester) && attempts < 20);
                
                if (!assignedTesters.contains(tester)) {
                    assignedTesters.add(tester);
                    BigDecimal testerWorkload = new BigDecimal(20 + random.nextInt(21)); // 20-40%
                    
                    createProjectMember(project, tester, testerWorkload, admin);
                }
            }
        }
        
        logger.info("Created project member assignments for {} projects", projects.size());
    }
    
    // Helper method to get available workload for a user (max 100%)
    private BigDecimal getAvailableWorkload(Long userId, Map<Long, BigDecimal> currentWorkloads, 
                                           int minWorkload, int maxWorkload) {
        BigDecimal currentWorkload = currentWorkloads.getOrDefault(userId, BigDecimal.ZERO);
        BigDecimal maxAllowed = new BigDecimal("100").subtract(currentWorkload);
        
        if (maxAllowed.compareTo(new BigDecimal(minWorkload)) < 0) {
            return BigDecimal.ZERO; // No available capacity
        }
        
        // Calculate desired workload within available capacity
        int desiredWorkload = minWorkload + (int)(Math.random() * (maxWorkload - minWorkload + 1));
        BigDecimal desired = new BigDecimal(desiredWorkload);
        
        // Return the minimum of desired workload and available capacity
        return desired.min(maxAllowed);
    }
    
    // Helper method to get an available user that hasn't been assigned to current project
    // and still has workload capacity
    private User getAvailableUser(List<User> users, Set<User> assignedUsers, 
                                 Map<Long, BigDecimal> currentWorkloads, Random random) {
        List<User> availableUsers = users.stream()
            .filter(user -> !assignedUsers.contains(user))
            .filter(user -> {
                BigDecimal currentWorkload = currentWorkloads.getOrDefault(user.getId(), BigDecimal.ZERO);
                return currentWorkload.compareTo(new BigDecimal("95")) < 0; // Leave at least 5% capacity
            })
            .collect(Collectors.toList());
        
        if (availableUsers.isEmpty()) {
            return null;
        }
        
        return availableUsers.get(random.nextInt(availableUsers.size()));
    }
    
    private ProjectMember createProjectMember(Project project, User user, BigDecimal workload, User createdBy) {
        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(user);
        member.setWorkloadPercentage(workload);
        member.setJoinedDate(project.getStartDate());
        member.setIsActive(true);
        member.setCreatedBy(createdBy);
        return projectMemberRepository.save(member);
    }
    
    private void initializeWorkLogs(List<User> users, List<Project> projects) {
        logger.info("Creating sample work logs...");
        
        // Generate work logs for the past 2 weeks for active project members
        // Admin (users.get(0)) is excluded from work logs as they don't participate in projects
        LocalDate today = LocalDate.now();
        LocalDate twoWeeksAgo = today.minusWeeks(2);
        
        User pm1 = users.get(1); // PM1
        User pm2 = users.get(2); // PM2
        
        // Work logs for Project 1 (Hệ Thống Quản Lý Bán Hàng Online) - including PM1
        createWorkLogsForPeriod(pm1, projects.get(0), twoWeeksAgo, today, "Quản Lý Dự Án & Lập Kế Hoạch");
        createWorkLogsForPeriod(users.get(3), projects.get(0), twoWeeksAgo, today, "Phát Triển Giao Diện React");
        createWorkLogsForPeriod(users.get(4), projects.get(0), twoWeeksAgo, today, "Phát Triển API Backend");
        createWorkLogsForPeriod(users.get(7), projects.get(0), twoWeeksAgo, today, "Phân Tích Yêu Cầu & Thiết Kế");
        createWorkLogsForPeriod(users.get(9), projects.get(0), twoWeeksAgo, today, "Kiểm Thử & Đảm Bảo Chất Lượng");
        
        // Work logs for Project 2 (Ứng Dụng Mobile Banking) - including PM2
        createWorkLogsForPeriod(pm2, projects.get(1), twoWeeksAgo, today, "Quản Lý Dự Án & Điều Phối");
        createWorkLogsForPeriod(users.get(5), projects.get(1), twoWeeksAgo, today, "Phát Triển Core Banking System");
        createWorkLogsForPeriod(users.get(6), projects.get(1), twoWeeksAgo, today, "Xây Dựng Ứng Dụng Mobile");
        createWorkLogsForPeriod(users.get(8), projects.get(1), twoWeeksAgo, today, "Thiết Kế Quy Trình Nghiệp Vụ");
        createWorkLogsForPeriod(users.get(10), projects.get(1), twoWeeksAgo, today, "Kiểm Thử Bảo Mật & Tích Hợp");
        
        // Work logs for Project 3 (Hệ Thống ERP Doanh Nghiệp) - including PM1
        createWorkLogsForPeriod(pm1, projects.get(2), twoWeeksAgo, today, "Giám Sát Dự Án & Chiến Lược");
        createWorkLogsForPeriod(users.get(3), projects.get(2), twoWeeksAgo, today, "Phát Triển Module ERP");
        createWorkLogsForPeriod(users.get(4), projects.get(2), twoWeeksAgo, today, "Tích Hợp Dữ Liệu & API");
        createWorkLogsForPeriod(users.get(7), projects.get(2), twoWeeksAgo, today, "Phân Tích Yêu Cầu ERP");
        
        // Work logs for Project 4 (Nền Tảng Học Trực Tuyến) - including PM2
        createWorkLogsForPeriod(pm2, projects.get(3), twoWeeksAgo, today, "Quản Lý Dự Án E-Learning");
        createWorkLogsForPeriod(users.get(5), projects.get(3), twoWeeksAgo, today, "Phát Triển Hệ Thống Giáo Dục");
        createWorkLogsForPeriod(users.get(8), projects.get(3), twoWeeksAgo, today, "Phân Tích Quy Trình Học Tập");
        createWorkLogsForPeriod(users.get(10), projects.get(3), twoWeeksAgo, today, "Kiểm Thử Video Conference");
        
        // Work logs for Project 5 (Hệ Thống IoT Thông Minh) - including PM1
        createWorkLogsForPeriod(pm1, projects.get(4), twoWeeksAgo, today, "Quản Lý Dự Án IoT & AI");
        createWorkLogsForPeriod(users.get(6), projects.get(4), twoWeeksAgo, today, "Phát Triển AI/ML & IoT");
        createWorkLogsForPeriod(users.get(9), projects.get(4), twoWeeksAgo, today, "Kiểm Thử Mô Hình AI");
        
        logger.info("Created sample work logs for the past 2 weeks");
    }
    
    private void createWorkLogsForPeriod(User user, Project project, LocalDate startDate, LocalDate endDate, String taskFeature) {
        LocalDate current = startDate;
        String[] workDescriptions = {
            "Triển khai tính năng mới và giải quyết các vấn đề kỹ thuật phức tạp",
            "Thực hiện code review và cập nhật tài liệu kỹ thuật",
            "Sửa lỗi hệ thống và tối ưu hóa hiệu năng ứng dụng",
            "Phát triển chức năng mới và thực hiện unit testing",
            "Kiểm thử tích hợp và chuẩn bị triển khai production",
            "Nghiên cứu công nghệ và phân tích giải pháp kỹ thuật",
            "Họp với khách hàng và thu thập yêu cầu nghiệp vụ",
            "Tái cấu trúc code và cải thiện kiến trúc hệ thống"
        };
        
        while (current.isBefore(endDate)) {
            // Skip weekends
            if (current.getDayOfWeek().getValue() <= 5) {
                WorkLog workLog = new WorkLog();
                workLog.setUser(user);
                workLog.setProject(project);
                workLog.setWorkDate(current);
                workLog.setHoursWorked(new BigDecimal("8.00"));
                workLog.setTaskFeature(taskFeature);
                workLog.setWorkDescription(workDescriptions[current.getDayOfMonth() % workDescriptions.length]);
                workLogRepository.save(workLog);
            }
            current = current.plusDays(1);
        }
    }
    
    private void initializeNotifications(List<User> users, List<Project> projects) {
        logger.info("Creating sample notifications...");
        
        // Welcome notifications for all non-admin users
        for (int i = 1; i < users.size(); i++) {
            User user = users.get(i);
            Notification welcome = new Notification();
            welcome.setUser(user);
            welcome.setTitle("Chào Mừng Đến Với Hệ Thống V-GOV");
            welcome.setMessage("Chào mừng bạn đến với hệ thống quản lý dự án V-GOV. Bạn có thể theo dõi công việc, xem chi tiết dự án và quản lý hồ sơ cá nhân.");
            welcome.setNotificationType("WELCOME");
            welcome.setIsRead(false);
            notificationRepository.save(welcome);
        }
        
        // Project assignment notifications
        createNotification(users.get(3), "Phân Công Dự Án", 
                          "Bạn đã được phân công vào dự án: Hệ Thống Quản Lý Bán Hàng Online", 
                          "PROJECT_ASSIGNMENT", projects.get(0), null);
        
        createNotification(users.get(4), "Phân Công Dự Án", 
                          "Bạn đã được phân công vào dự án: Hệ Thống Quản Lý Bán Hàng Online", 
                          "PROJECT_ASSIGNMENT", projects.get(0), null);
        
        createNotification(users.get(5), "Phân Công Dự Án", 
                          "Bạn đã được phân công vào dự án: Ứng Dụng Mobile Banking", 
                          "PROJECT_ASSIGNMENT", projects.get(1), null);
        
        // Project status change notifications
        for (User user : List.of(users.get(5), users.get(8), users.get(10))) {
            createNotification(user, "Cập Nhật Trạng Thái Dự Án", 
                              "Dự án 'Nền Tảng Học Trực Tuyến' đã được chuyển sang trạng thái 'Tạm Dừng'", 
                              "PROJECT_STATUS_CHANGE", projects.get(3), null);
        }
        
        // Workload reminder notifications
        createNotification(users.get(3), "Thông Báo Workload", 
                          "Workload hiện tại của bạn là 100%. Vui lòng đảm bảo cập nhật work log đầy đủ.", 
                          "WORKLOAD_REMINDER", null, null);
        
        createNotification(users.get(10), "Thông Báo Workload", 
                          "Workload hiện tại của bạn là 100%. Vui lòng đảm bảo cập nhật work log đầy đủ.", 
                          "WORKLOAD_REMINDER", null, null);
        
        logger.info("Created sample notifications");
    }
    
    private void createNotification(User user, String title, String message, String type, Project project, User relatedUser) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(type);
        notification.setRelatedProject(project);
        notification.setRelatedUser(relatedUser);
        notification.setIsRead(false);
        notificationRepository.save(notification);
    }
    
    private void logLoginCredentials() {
        logger.info("\n" + "=".repeat(80));
        logger.info("THÔNG TIN ĐĂNG NHẬP MẪU CHO HỆ THỐNG V-GOV");
        logger.info("=".repeat(80));
        logger.info("Quản Trị Viên:");
        logger.info("  Email: admin@vgov.vn");
        logger.info("  Mật khẩu: 123456");
        logger.info("");
        logger.info("Quản Lý Dự Án:");
        logger.info("  Email: pm1@vgov.vn | Mật khẩu: 123456");
        logger.info("  Email: pm2@vgov.vn | Mật khẩu: 123456");
        logger.info("");
        logger.info("Lập Trình Viên:");
        logger.info("  Email: dev1@vgov.vn | Mật khẩu: 123456");
        logger.info("  Email: dev2@vgov.vn | Mật khẩu: 123456");
        logger.info("  Email: dev3@vgov.vn | Mật khẩu: 123456");
        logger.info("  Email: dev4@vgov.vn | Mật khẩu: 123456");
        logger.info("");
        logger.info("Phân Tích Viên:");
        logger.info("  Email: ba1@vgov.vn | Mật khẩu: 123456");
        logger.info("  Email: ba2@vgov.vn | Mật khẩu: 123456");
        logger.info("");
        logger.info("Kiểm Thử Viên:");
        logger.info("  Email: test1@vgov.vn | Mật khẩu: 123456");
        logger.info("  Email: test2@vgov.vn | Mật khẩu: 123456");
        logger.info("=".repeat(80));
    }
}
