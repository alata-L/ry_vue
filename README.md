## 平台简介

* 前端采用Vue、Element UI。
* 后端采用Spring Boot、Spring Security、Redis & Jwt。
* 权限认证使用Jwt，支持多终端认证系统。
* 支持加载动态权限菜单，多方式轻松权限控制。

## 内置功能

1.  用户管理：用户是系统操作者，该功能主要完成系统用户配置。
2.  部门管理：配置系统组织机构（公司、部门、小组），树结构展现支持数据权限。
3.  岗位管理：配置系统用户所属担任职务。
4.  菜单管理：配置系统菜单，操作权限，按钮权限标识等。
5.  角色管理：角色菜单权限分配、设置角色按机构进行数据范围权限划分。
6.  字典管理：对系统中经常使用的一些较为固定的数据进行维护。
7.  参数管理：对系统动态配置常用参数。
8.  通知公告：系统通知公告信息发布维护。
9.  操作日志：系统正常操作日志记录和查询；系统异常信息日志记录和查询。
10. 登录日志：系统登录日志记录查询包含登录异常。
11. 在线用户：当前系统中活跃用户状态监控。
12. 定时任务：在线（添加、修改、删除）任务调度包含执行结果日志。
13. 系统接口：根据业务代码自动生成相关的api接口文档。
14. 服务监控：监视当前系统CPU、内存、磁盘、堆栈等相关信息。
15. 缓存监控：对系统的缓存信息查询，命令统计等。
16. 在线构建器：拖动表单元素生成相应的HTML代码。
17. 连接池监视：监视当前系统数据库连接池状态，可进行分析SQL找出系统性能瓶颈。

## 接口文档
http://localhost:8080/swagger-ui/index.html

## 环境要求

- JDK 1.8+
- Maven 3.0+
- MySQL 5.7+
- Redis 3.0+
- Node.js 8.9+
- npm 3.0+

## 启动步骤

### 1. 数据库准备

1. 创建数据库 `ry-vue`（MySQL）
2. 执行SQL脚本（按顺序执行）：
   - `sql/ry_20250522.sql` - 主数据库脚本
   - `sql/quartz.sql` - 定时任务表
   - `sql/custom.sql` - 定制业务表（可选）

### 2. 后端配置

修改 `ruoyi-admin/src/main/resources/application-druid.yml` 中的数据库配置：

```yaml
spring:
    datasource:
        druid:
            master:
                url: jdbc:mysql://localhost:3306/ry-vue?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
                username: root
                password: root  # 修改为你的数据库密码
```

修改 `ruoyi-admin/src/main/resources/application.yml` 中的Redis配置（如需要）：

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password:  # 如有密码请填写
```

### 3. 启动后端

**方式一：使用IDE启动**

1. 使用IDEA或Eclipse导入项目
2. 找到主启动类：`ruoyi-admin/src/main/java/com/ruoyi/RuoYiApplication.java`
3. 右键运行 `main` 方法

**方式二：使用Maven命令启动**

```bash
# 进入项目根目录
cd springboot-cms

# 编译项目
mvn clean install

# 进入admin模块
cd ruoyi-admin

# 启动项目
mvn spring-boot:run
```

**后端启动成功标志：**
- 控制台输出 "启动成功"
- 访问 http://localhost:8080 可看到接口响应

**后端访问地址：**
- 应用地址：http://localhost:8080
- API文档：http://localhost:8080/swagger-ui/index.html
- Druid监控：http://localhost:8080/druid/

### 4. 启动前端

```bash
# 进入前端目录
cd ruoyi-ui

# 安装依赖（首次运行需要）
npm install

# 如果npm安装慢，可以使用国内镜像
npm install --registry=https://registry.npmmirror.com

# 启动开发服务器
npm run dev
```

**前端启动成功标志：**
- 控制台显示编译成功信息
- 浏览器自动打开 http://localhost:80

**前端访问地址：**
- 开发地址：http://localhost:80

### 5. 默认登录信息

- 用户名：`admin`
- 密码：`admin123`（请根据数据库实际数据确认）

## 常见问题

1. **数据库连接失败**
   - 检查MySQL服务是否启动
   - 检查数据库用户名密码是否正确
   - 检查数据库 `ry-vue` 是否已创建

2. **Redis连接失败**
   - 检查Redis服务是否启动
   - 检查Redis配置是否正确

3. **前端启动失败**
   - 检查Node.js版本是否符合要求（>=8.9）
   - 删除 `node_modules` 文件夹后重新执行 `npm install`
   - 检查端口80是否被占用

4. **后端启动失败**
   - 检查JDK版本是否为1.8+
   - 检查Maven配置是否正确
   - 检查数据库和Redis是否正常启动