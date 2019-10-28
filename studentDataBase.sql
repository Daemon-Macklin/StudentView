-- phpMyAdmin SQL Dump
-- version 4.9.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Oct 28, 2019 at 02:01 AM
-- Server version: 10.4.8-MariaDB
-- PHP Version: 7.3.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `studentDataBase`
--

-- --------------------------------------------------------

--
-- Table structure for table `students`
--

CREATE TABLE `students` (
  `SID` int(5) NOT NULL,
  `STUD_ID` int(8) NOT NULL,
  `FNAME` varchar(20) COLLATE sjis_bin NOT NULL,
  `SNAME` varchar(20) COLLATE sjis_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=sjis COLLATE=sjis_bin;

--
-- Dumping data for table `students`
--

INSERT INTO `students` (`SID`, `STUD_ID`, `FNAME`, `SNAME`) VALUES
(12345, 12345678, 'Daemon', 'Macklin'),
(43563, 12342257, 'Jerry', 'Hood'),
(44444, 44444444, 'Chris', 'Phillips'),
(54321, 24689753, 'John', 'Goodman'),
(55555, 55555555, 'Justin', 'Falk'),
(99999, 9999999, 'Bob', 'Builder');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `UID` int(5) NOT NULL,
  `UNAME` varchar(20) COLLATE sjis_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=sjis COLLATE=sjis_bin;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`UID`, `UNAME`) VALUES
(1234, 'Daemon');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `students`
--
ALTER TABLE `students`
  ADD PRIMARY KEY (`SID`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`UID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
