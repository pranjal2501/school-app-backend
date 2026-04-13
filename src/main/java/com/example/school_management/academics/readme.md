# Academics Module 📚

This module acts as the orchestrator for the school's curriculum, teacher qualifications, and active classroom timetables. It manages the complex intersection between `SchoolClass`, `Subject`, and `Teacher`.

## 🏗 Core Architecture: The "Template-to-Instance" Pattern

In a real-world school, all divisions of a specific Standard (e.g., 10-A, 10-B, 10-C) generally follow the exact same curriculum. However, individual classes may have different teachers for those subjects.

To make data entry rapid for administrators while preserving flexibility, this module implements a **Template-to-Instance** architectural pattern.

### 1. The Pivot Entity: `ClassSubjectTeacher` (The Reality Table)
At the heart of this module is the pivot table (`class_subject_teachers`). We do not map Teachers directly to Subjects, nor do we map Subjects directly to Classes.

Instead, we map a Teacher to the **intersection** of a Class and a Subject.

**Table Structure:**
* `class_id` (e.g., Class 10-A)
* `subject_id` (e.g., Mathematics)
* `teacher_id` (e.g., Mr. Smith)

### 2. Standard Template Definition (The UI Workflow)
When setting up a new academic year, the Admin uses the UI to select a **Standard** (e.g., `STD_10`).
They are presented with an interface to define the curriculum (the "Template"). For each new subject, they define:
* Subject Name & Code
* Marking Scheme (Theory, Practical, Total, Passing)
* Subject Type (Compulsory/Optional)
* **Default Subject Teacher** (e.g., Mr. Smith)

### 3. Bulk Application (Template -> Instance)
When the Admin saves the template, the backend dynamically finds **all** existing classes that belong to that Standard (e.g., 10-A, 10-B) and bulk-populates the pivot table.

**Database State after Bulk Application:**
| Class | Subject | Teacher |
| :--- | :--- | :--- |
| `CLASS_10_A` | Math | Mr. Smith |
| `CLASS_10_A` | Science | Mr. Smith |
| `CLASS_10_A` | English | Mr. Steve |
| `CLASS_10_B` | Math | Mr. Smith |
| `CLASS_10_B` | Science | Mr. Smith |
| `CLASS_10_B` | English | Mr. Steve |

*(Notice how 10-A and 10-B are identical at this stage, mirroring the master template).*

### 4. Granular Class Overrides
Real-world scheduling is messy. Mr. Steve might not have the bandwidth to teach English to *every* 10th-grade class.

The system provides an **Override Endpoint** allowing the Admin to modify the template for a *specific* class without affecting the others. For example, the Admin can swap Mr. Steve for Mr. Jonathan, but *only* for Class 10-B.

**Database State after Override:**
| Class | Subject | Teacher |
| :--- | :--- | :--- |
| `CLASS_10_A` | Math | Mr. Smith |
| `CLASS_10_A` | Science | Mr. Smith |
| `CLASS_10_A` | English | Mr. Steve |
| `CLASS_10_B` | Math | Mr. Smith |
| `CLASS_10_B` | Science | Mr. Smith |
| `CLASS_10_B` | **English** | **Mr. Jonathan** *(Override applied)* |
| `CLASS_10_B` | **French** | **Mr. Steve** *(New subject assigned only to 10-B)* |

---

## 🚀 Key Endpoints

* `POST /api/v1/academic-setup/standards/template`
    * Accepts the master payload of subjects and default teachers. Applies them to all classes matching the requested standard.
* `PATCH /api/v1/academic-setup/classes/{classId}/override`
    * Updates the `ClassSubjectTeacher` pivot table for a specific class, allowing the admin to swap out the default teacher for a specific instance.