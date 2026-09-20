# Project Rules: School Medical Management System

## Core Behavioral Constraints for AI Agents

1. **Plan-First Principle**:
   - Never implement code or modify files without first creating a detailed plan (`docs/plans/...` or `implementation_plan.md`).
   - You must obtain the user's explicit approval before proceeding to execution.

2. **Ask Before Deciding (Options & Recommendations)**:
   - If there are multiple implementation paths, technical trade-offs, or unexpected issues arising during execution:
     - DO NOT decide unilaterally.
     - You MUST ask the user.
     - When asking, clearly present the context, provide at least 2 viable options with Pros/Cons/Risks, and give a clear recommended choice with rationale.

3. **No Unauthorized Git Push**:
   - You are allowed to run `git status`, `git diff`, and create clean local commits.
   - You MUST NEVER execute `git push` to GitHub or any remote repository without explicit user permission/instruction.

4. **Scope Discipline**:
   - Only modify files directly related to the approved task.
   - Do NOT clean up, reformat, or refactor unrelated adjacent code.
   - Preserve existing code comments and docstrings.

5. **Mandatory Verification Gate**:
   - Always verify changes with `.\mvnw clean test-compile` (or relevant test suites).
   - Only declare a step or phase complete when the build succeeds with 0 errors (`BUILD SUCCESS`).

6. **Security & Secrets Hygiene**:
   - Never hardcode passwords, emails, API keys, or database credentials.
   - Always use environment variables and ensure secret files (`.env`, `application-dev.properties`) are in `.gitignore`.

7. **Documentation Sync**:
   - Always keep `docs/plans/master-upgrade-plan.md` updated with progress.
   - Provide manual verification steps for the user after completing each phase.
