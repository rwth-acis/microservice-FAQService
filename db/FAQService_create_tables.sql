--
-- Database Schema:  faq 
-- Automatically generated sql script for the service FAQService, created by the CAE.
-- --------------------------------------------------------

--
-- Table structure for table entry.
--
CREATE TABLE faq.entry (
  answer VARCHAR(255),
  ID int NOT NULL AUTO_INCREMENT,
  question VARCHAR(255),
  CONSTRAINT ID_PK PRIMARY KEY (ID)
);

