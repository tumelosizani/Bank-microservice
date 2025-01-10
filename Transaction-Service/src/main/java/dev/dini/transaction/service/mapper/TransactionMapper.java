package dev.dini.transaction.service.mapper;

import dev.dini.transaction.service.dto.TransactionRequestDTO;
import dev.dini.transaction.service.dto.TransactionResponseDTO;
import dev.dini.transaction.service.transaction.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    //Map TransactionRequestDTO to Transaction entity
    Transaction toTransaction(TransactionRequestDTO transactionRequestDTO);


    // Map Transaction entity to TransactionResponseDTO
    @Mapping(target = "timestamp", source = "timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    TransactionResponseDTO toTransactionResponseDTO(Transaction transaction);

    // Map a list of Transactions to a list of TransactionResponseDTO
    List<TransactionResponseDTO> toTransactionResponseDTOList(List<Transaction> transactions);

}
