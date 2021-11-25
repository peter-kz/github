## IMPORTANT! Please go through the following Application Security Checklist before commit changes
- [ ] BAC: The Business Requirements for Authentication & Authorization have been met. If there are no business requirements deny-by-default strategy have been applied.
  -  https://owasp.org/Top10/A01_2021-Broken_Access_Control/
  -  https://cheatsheetseries.owasp.org/cheatsheets/Authorization_Cheat_Sheet.html	
- [ ] XSS: Escape user input using language-specific or framework-specific instruments 
- [ ] XSS: Know all the locations where user input is used, and try to avoid returning unsanitized user input to potentially dangerous locations like HTML body and attributes, javascript, GET parameters, URLs, links, CSS.
  - https://cheatsheetseries.owasp.org/cheatsheets/Cross_Site_Scripting_Prevention_Cheat_Sheet.html
- [ ] Make sure that your code doesn't contain ANY secrets.
- [ ] Make sure that you've removed all debug code.
